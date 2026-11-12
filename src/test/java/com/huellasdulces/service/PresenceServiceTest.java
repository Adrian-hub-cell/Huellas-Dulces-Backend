
package com.huellasdulces.service;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private PresenceService presenceService;

    @BeforeEach
    void setUp() {
        presenceService = new PresenceService(messagingTemplate);
    }

    @Test
    void shouldAddUserWhenConnected() {
        Principal user = () -> "adrian";

        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, "session-1", null),
                        user
                )
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                List.of("adrian")
        );
    }

    @Test
    void shouldIgnoreConnectionWithoutUser() {
        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, "session-1", null)
                )
        );

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void shouldIgnoreConnectionWithoutSessionId() {
        Principal user = () -> "adrian";

        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, null, null),
                        user
                )
        );

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void shouldIgnoreDisconnectWithoutUser() {
        presenceService.handleDisconnect(
                new SessionDisconnectEvent(
                        this,
                        createMessage(StompCommand.DISCONNECT, "session-1", null),
                        "session-1",
                        CloseStatus.NORMAL
                )
        );

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void shouldRemoveUserWhenLastSessionDisconnects() {
        Principal user = () -> "adrian";

        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, "session-1", null),
                        user
                )
        );

        reset(messagingTemplate);

        presenceService.handleDisconnect(
                new SessionDisconnectEvent(
                        this,
                        createMessage(StompCommand.DISCONNECT, "session-1", null),
                        "session-1",
                        CloseStatus.NORMAL,
                        user
                )
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                List.of()
        );
    }

    @Test
    void shouldKeepUserOnlineWhenAnotherSessionRemains() {
        Principal user = () -> "adrian";

        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, "session-1", null),
                        user
                )
        );

        presenceService.handleConnect(
                new SessionConnectedEvent(
                        this,
                        createMessage(StompCommand.CONNECTED, "session-2", null),
                        user
                )
        );

        reset(messagingTemplate);

        presenceService.handleDisconnect(
                new SessionDisconnectEvent(
                        this,
                        createMessage(StompCommand.DISCONNECT, "session-1", null),
                        "session-1",
                        CloseStatus.NORMAL,
                        user
                )
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                List.of("adrian")
        );
    }

    @Test
    void shouldBroadcastWhenUnknownUserDisconnects() {
        Principal user = () -> "desconocido";

        presenceService.handleDisconnect(
                new SessionDisconnectEvent(
                        this,
                        createMessage(StompCommand.DISCONNECT, "session-1", null),
                        "session-1",
                        CloseStatus.NORMAL,
                        user
                )
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                List.of()
        );
    }

    @Test
    void shouldBroadcastWhenUserSubscribesToPresenceTopic() {
        presenceService.handleSubscribe(
                new SessionSubscribeEvent(
                        this,
                        createMessage(
                                StompCommand.SUBSCRIBE,
                                "session-1",
                                "/topic/presence"
                        )
                )
        );

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                List.of()
        );
    }

    @Test
    void shouldNotBroadcastWhenUserSubscribesToAnotherTopic() {
        presenceService.handleSubscribe(
                new SessionSubscribeEvent(
                        this,
                        createMessage(
                                StompCommand.SUBSCRIBE,
                                "session-1",
                                "/topic/products"
                        )
                )
        );

        verifyNoInteractions(messagingTemplate);
    }

    private Message<byte[]> createMessage(
            StompCommand command,
            String sessionId,
            String destination) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(command);

        if (sessionId != null) {
            accessor.setSessionId(sessionId);
        }

        if (destination != null) {
            accessor.setDestination(destination);
        }

        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
    }
}
