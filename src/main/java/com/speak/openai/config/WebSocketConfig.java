package com.speak.openai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatSocketHandler chatSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns("*");
    }


    @RequiredArgsConstructor
    @Component
    public static class ChatSocketHandler extends TextWebSocketHandler {
        private final OpenAiChatModel openAiChatModel;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            session.sendMessage(new TextMessage("연결되었습니다.\n"));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            if(message.getPayload().isEmpty()) return;
            Prompt prompt = new Prompt(new UserMessage(message.getPayload()));
            Flux<ChatResponse> result = openAiChatModel.stream(prompt);
            result.subscribe(chatResponse -> {
                try {
                    String responseText = chatResponse.getResult().getOutput().getContent();
                    session.sendMessage(new TextMessage(responseText));
                } catch (Exception e) {}
            },error -> {
                error.printStackTrace();
            }, () -> {
                try {
                    session.sendMessage(new TextMessage("\n"));
                } catch (IOException e) {}
            });
        }
    }
}
