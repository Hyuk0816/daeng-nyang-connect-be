package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // WebSocket에 대한 보안 규칙을 설정합니다.
        messages.simpDestMatchers("/websocket/**").permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Cross-Origin 요청을 허용합니다.
        return true;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("http:localhost:3000/**")
                .withSockJS();
    }
}
