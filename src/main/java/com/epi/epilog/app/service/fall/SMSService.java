package com.epi.epilog.app.service.fall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSService {
    @Value("${sms.cool.api}")
    private String apiKey;

    @Value("${sms.cool.secret}")
    private String apiSecret;

    /**
     * 메시지 전송
     *
     * @param to   받는 사람
     * @param from 보내는 사람
     * @param text 내용
     * @throws Exception
     */
    public void sendSms(String to, String from, String text) throws Exception {
        DefaultMessageService messageService = NurigoApp.INSTANCE
                .initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

        Message message = new Message();

        message.setFrom(from);
        message.setTo(to);
        message.setText(text);

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}