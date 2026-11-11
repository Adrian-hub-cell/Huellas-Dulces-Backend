package com.huellasdulces.service;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class PresenceService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ConcurrentHashMap<String, Set<String>> sessions =
            new ConcurrentHashMap<>();

    public PresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal user = event.getUser();
        String sessionId = accessor.getSessionId();

        if (user == null || sessionId == null) {
            System.out.println("[Presence] Conexión sin usuario o sesión.");
            return;
        }

        sessions.computeIfAbsent(
                user.getName(),
                key -> ConcurrentHashMap.newKeySet()
        ).add(sessionId);

        System.out.println("[Presence] Conectado: " + user.getName());
        broadcastPresence();
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();

        if (user == null) {
            return;
        }

        Set<String> userSessions = sessions.get(user.getName());

        if (userSessions != null) {
            userSessions.remove(event.getSessionId());

            if (userSessions.isEmpty()) {
                sessions.remove(user.getName(), userSessions);
            }
        }

        System.out.println("[Presence] Desconectado: " + user.getName());
        broadcastPresence();
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        if ("/topic/presence".equals(accessor.getDestination())) {
            System.out.println("[Presence] Suscripción al canal de presencia.");
            broadcastPresence();
        }
    }

    private void broadcastPresence() {
        var onlineUsers = sessions.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("[Presence] Usuarios conectados: " + onlineUsers);

        messagingTemplate.convertAndSend(
                "/topic/presence",
                onlineUsers
        );
    }
}