package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.member.FCMToken;
import com.epi.epilog.app.repository.FCMRepository;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final FCMRepository fcmRepository;
//    public void sendMessageTo(final Long receiverId, final Notification notification) {
//
//        final FCMToken fcmToken = fcmRepository.findByMemberId(receiverId)
//                .orElseThrow(() -> new NotificationException(NOT_FOUND_FCM_TOKEN));
//
//        //메시지 만들기
//        final String message = makeMessage(fcmToken.getToken(), notification);
//
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        //OAuth 2.0 사용
//        httpHeaders.add(HttpHeaders.AUTHORIZATION, PREFIX_ACCESS_TOKEN + getAccessToken());
//
//        final HttpEntity<String> httpEntity = new HttpEntity<>(message, httpHeaders);
//
//        final String fcmRequestUrl = PREFIX_FCM_REQUEST_URL + projectId + POSTFIX_FCM_REQUEST_URL;
//
//        final ResponseEntity<String> exchange = restTemplate.exchange(
//                fcmRequestUrl,
//                HttpMethod.POST,
//                httpEntity,
//                String.class
//        );
//
//        if (exchange.getStatusCode().isError()) {
//            log.error("firebase 접속 에러 = {}", exchange.getBody());
//        }
//    }
//
//    private String makeMessage(final String targetToken, final Notification notification) {
//
//        final Long senderId = notification.getSenderId();
//        final Member sender = memberRepository.findById(senderId)
//                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
//
//        final Data messageData = new Data(
//                sender.getName(), senderId.toString(),
//                notification.getReceiverId().toString(), notification.getMessage(),
//                sender.getOpenProfileUrl()
//        );
//
//        final Message message = new Message(messageData, targetToken);
//
//        final FcmMessage fcmMessage = new FcmMessage(DEFAULT_VALIDATE_ONLY, message);
//
//        try {
//            return objectMapper.writeValueAsString(fcmMessage);
//        } catch (JsonProcessingException e) {
//            log.error("메세지 보낼 때 JSON 변환 에러", e);
//            throw new NotificationException(CONVERTING_JSON_ERROR);
//        }
//    }
}
