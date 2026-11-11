package com.huellasdulces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public WebSocketConfig(
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(
                    Message<?> message,
                    MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(
                                message,
                                StompHeaderAccessor.class
                        );

                if (accessor == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authorization =
                            accessor.getFirstNativeHeader("Authorization");

                    if (authorization == null ||
                            !authorization.startsWith("Bearer ")) {
                        throw new BadCredentialsException(
                                "Token JWT requerido para conectar por STOMP"
                        );
                    }

                    String token = authorization.substring(7);

                    try {
                        Jwt jwt = jwtDecoder.decode(token);

                        Authentication authentication =
                                jwtAuthenticationConverter.convert(jwt);

                        if (authentication == null ||
                                !authentication.isAuthenticated()) {
                            throw new BadCredentialsException(
                                    "Token JWT no válido"
                            );
                        }

                        accessor.setUser(authentication);

                    } catch (JwtException exception) {
                        throw new BadCredentialsException(
                                "Token JWT inválido o expirado",
                                exception
                        );
                    }
                }

                return message;
            }
        });
    }
}

