
package com.huellasdulces.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    static class TestChannelRegistration extends ChannelRegistration {
        public List<ChannelInterceptor> exposedInterceptors() {
            return super.getInterceptors();
        }
    }

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Mock
    private MessageChannel channel;

    private ChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        WebSocketConfig config = new WebSocketConfig(
                jwtDecoder,
                jwtAuthenticationConverter
        );

        TestChannelRegistration registration =
                new TestChannelRegistration();

        config.configureClientInboundChannel(registration);
        interceptor = registration.exposedInterceptors().get(0);
    }

    private Message<byte[]> connectMessage(String authorization) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(StompCommand.CONNECT);

        if (authorization != null) {
            accessor.setNativeHeader("Authorization", authorization);
        }

        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
    }

    @Test
    void shouldReturnMessageWhenAccessorIsNull() {
        Message<byte[]> message =
                MessageBuilder.withPayload(new byte[0]).build();

        assertSame(message, interceptor.preSend(message, channel));
        verifyNoInteractions(jwtDecoder, jwtAuthenticationConverter);
    }

    @Test
    void shouldAllowMessagesThatAreNotConnect() {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(StompCommand.SEND);

        Message<byte[]> message = MessageBuilder.createMessage(
                new byte[0], accessor.getMessageHeaders()
        );

        assertSame(message, interceptor.preSend(message, channel));
        verifyNoInteractions(jwtDecoder, jwtAuthenticationConverter);
    }

    @Test
    void shouldRejectConnectWithoutAuthorizationHeader() {
        Message<byte[]> message = connectMessage(null);

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(message, channel));

        verifyNoInteractions(jwtDecoder);
    }

    @Test
    void shouldRejectConnectWithInvalidAuthorizationPrefix() {
        Message<byte[]> message = connectMessage("Basic abc123");

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(message, channel));

        verifyNoInteractions(jwtDecoder);
    }

    @Test
    void shouldAuthenticateConnectWithValidToken() {
        Message<byte[]> message = connectMessage("Bearer valid-token");

        Jwt jwt = Jwt.withTokenValue("valid-token")
                .header("alg", "none")
                .subject("adrian")
                .build();

        AbstractAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "adrian", null, List.of()
                );

        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
        when(jwtAuthenticationConverter.convert(jwt))
                .thenReturn(authentication);

        Message<?> result = interceptor.preSend(message, channel);

        assertSame(message, result);

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        result, StompHeaderAccessor.class
                );

        assertNotNull(accessor);
        assertSame(authentication, accessor.getUser());
    }

    @Test
    void shouldRejectWhenAuthenticationIsNull() {
        Message<byte[]> message = connectMessage("Bearer valid-token");

        Jwt jwt = Jwt.withTokenValue("valid-token")
                .header("alg", "none")
                .subject("adrian")
                .build();

        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
        when(jwtAuthenticationConverter.convert(jwt)).thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(message, channel));
    }

    @Test
    void shouldRejectWhenAuthenticationIsNotAuthenticated() {
        Message<byte[]> message = connectMessage("Bearer valid-token");

        Jwt jwt = Jwt.withTokenValue("valid-token")
                .header("alg", "none")
                .subject("adrian")
                .build();

        AbstractAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "adrian", null
                );

        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
        when(jwtAuthenticationConverter.convert(jwt))
                .thenReturn(authentication);

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(message, channel));
    }

    @Test
    void shouldRejectWhenJwtDecoderThrowsException() {
        Message<byte[]> message = connectMessage("Bearer expired-token");

        when(jwtDecoder.decode("expired-token"))
                .thenThrow(new JwtException("Token expirado"));

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(message, channel));
    }
}
