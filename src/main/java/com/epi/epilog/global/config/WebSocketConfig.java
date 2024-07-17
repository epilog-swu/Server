package com.epi.epilog.global.config;

import com.epi.epilog.global.socket.FallDetectionWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final FallDetectionWebSocketHandler fallDetectionWebSocketHandler;
    public WebSocketConfig(FallDetectionWebSocketHandler fallDetectionWebSocketHandler){
        this.fallDetectionWebSocketHandler = fallDetectionWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(fallDetectionWebSocketHandler, "/detection/fall").setAllowedOrigins("*");
    }
}
