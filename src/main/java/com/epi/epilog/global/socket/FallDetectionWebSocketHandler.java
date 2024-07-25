package com.epi.epilog.global.socket;

import com.epi.epilog.app.service.FallDetectionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.epi.epilog.app.dto.SensorData;
import com.epi.epilog.global.utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class FallDetectionWebSocketHandler extends TextWebSocketHandler {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FallDetectionService fallDetectionService;

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        String token = extractTokenFromUri(uri);

        if (token != null && jwtUtil.validateJwt(token)) {
            sessions.add(session);
            System.out.println("연결되었습니다: ID(" + session.getId()+")");
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            System.out.println("연결되지 않았습니다: ID(" + session.getId()+")");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        JsonNode jsonNode = objectMapper.readTree(payload);
        String event = jsonNode.get("event").asText();
        JsonNode data = jsonNode.get("data");

        // handler 추가 구현을 통해 이벤트 분기
        switch (event) {
            case "fall":
                handleFallEvent(session, data);
                break;
            case "pong":
                handlePongEvent(session, data);
                break;
            default:
                System.out.println("Unknown event: " + event);
                break;
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendPingEvent() {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    Map<String, Object> response = new HashMap<>();
                    response.put("event", "ping");
                    response.put("success", true);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePongEvent(WebSocketSession session, JsonNode data) {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("연결이 해제되었습니다: ID(" + session.getId() +")");
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

    private void handleFallEvent(WebSocketSession session, JsonNode data) throws Exception {
        JsonNode fallNode = data.get("fall");
        if (fallNode != null && fallNode.isArray()) {
            List<SensorData> fallData = objectMapper.readValue(fallNode.toString(), new TypeReference<List<SensorData>>() {});
            boolean fallDetectedResult = fallDetectionService.isFallDetected(fallData);
            System.out.println("return value: " + fallDetectedResult);

            Map<String, Object> response = new HashMap<>();

            if (fallDetectedResult == true) {
                response.put("event", "fall");
                response.put("message", "낙상이 감지되었습니다.");
                response.put("success", true);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            } else {
                response.put("event", "fall");
                response.put("success", false);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }
        }
    }
}
