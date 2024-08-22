package com.epi.epilog.global.socket;

import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.EmerData;
import com.epi.epilog.app.dto.SensorData;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.app.service.fall.EmergencyService;
import com.epi.epilog.app.service.fall.FallDetectionService;
import com.epi.epilog.app.service.fall.SMSService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class FallDetectionWebSocketHandler extends TextWebSocketHandler {
    @Value("${sms.server.phone}")
    private String SERVER_PHONE;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FallDetectionService fallDetectionService;
    private final EmergencyService emergencyService;
    private final SMSService smsService;
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final MemberRepository memberRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        String token = extractTokenFromUri(uri);

        if (token != null && jwtUtil.validateJwt(token)) {
            String memberId = jwtUtil.getUserById(token);
            if (memberId != null) {
                Member member = memberRepository.findById(Long.valueOf(memberId))
                        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

                CustomUserInfoDto userInfoDto = new CustomUserInfoDto(
                        member.getId(),
                        member.getLoginId(),
                        member.getPassword(),
                        member.getName(),
                        member.getCode()
                );
                UserDetails userDetails = new CustomUserDetails(userInfoDto);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                sessions.add(session);
                session.getAttributes().put("token", token);
                log.info("연결되었습니다: ID(" + session.getId() + ")");
            } else {
                log.error("Invalid token: " + token);
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            log.warn("연결되지 않았습니다: ID(" + session.getId() + ")");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: " + payload);

        JsonNode jsonNode = objectMapper.readTree(payload);
        String event = jsonNode.get("event").asText();
        JsonNode data = jsonNode.get("data");

        try {
            switch (event) {
                case "fall":
                    handleFallEvent(session, data);
                    break;
                case "emer":
                    String token = (String) session.getAttributes().get("token");
                    handleEmergencyEvent(token, session, data);
                    break;
                case "pong":
                    handlePongEvent(session, data);
                    break;
                default:
                    log.warn("Unknown event: " + event);
                    break;
            }
        } catch (ApiException e) {
            log.error("Error handling event: " + event, e);
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        }
    }

    private void handleEmergencyEvent(String token, WebSocketSession session, JsonNode data) throws Exception {
        try {
            if (!jwtUtil.validateJwt(token)) {
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }
            String userId = jwtUtil.getUserById(token);
            Member member = memberRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            EmerData emerData = objectMapper.readValue(data.toString(), new TypeReference<EmerData>() {});
            String addressStr = emergencyService.emerEvent(emerData);

            String message = member.getName() + "님 낙상 감지됨" + addressStr;
            // sms 전송
//            smsService.sendSms(member.getProtectorPhone(), SERVER_PHONE, message);

            Map<String, Object> response = new HashMap<>();
            response.put("event", "emer");
            response.put("success", true);
            response.put("message", message);

            if (session.isOpen()) {
                emergencyService.createLog(member, emerData);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                log.info("클라이언트에 메시지 전송");
            } else {
                log.warn("Session is closed, cannot send message: " + session.getId());
            }
        } catch (Exception e) {
            log.warn("claims exception: " + e);
        }
    }

    private void handleFallEvent(WebSocketSession session, JsonNode data) throws Exception {
        JsonNode fallNode = data.get("fall");
        if (fallNode != null && fallNode.isArray()) {
            List<SensorData> fallData = objectMapper.readValue(fallNode.toString(), new TypeReference<List<SensorData>>() {});
            boolean fallDetectedResult = fallDetectionService.isFallDetected(fallData);
            log.info("return value: " + fallDetectedResult);

            if (fallDetectedResult) {
                fallDetectedResult = fallDetectionService.isAIFallDetected(fallData);
                log.info("ai return value: " + fallDetectedResult);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("event", "fall");
            response.put("success", fallDetectedResult);
            response.put("message", fallDetectedResult ? "낙상이 감지되었습니다." : "낙상이 감지되지 않았습니다.");

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            } else {
                log.warn("Session is closed, cannot send message: " + session.getId());
            }
        } else {
            log.warn("Invalid fall data received: " + fallNode);
            Map<String, Object> response = new HashMap<>();
            response.put("event", "fall");
            response.put("success", false);
            response.put("message", "Invalid fall data received.");

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            } else {
                log.warn("Session is closed, cannot send message: " + session.getId());
            }
        }
    }

    private void handlePongEvent(WebSocketSession session, JsonNode data) {}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("연결이 해제되었습니다: ID(" + session.getId() + ")");
    }

    private String extractTokenFromUri(String uri) {
        String[] parts = uri.split("\\?");
        if (parts.length > 1) {
            String query = parts[1];
            String[] queryParams = query.split("&");
            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}
