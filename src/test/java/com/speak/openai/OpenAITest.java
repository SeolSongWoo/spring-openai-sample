package com.speak.openai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OpenAITest {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    @DisplayName("OpenAiChatModel call Method 테스트")
    void callTest() {
        String message = "Tell me a joke";
        String result = openAiChatModel.call(message);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("OpenAiChatModel stream Method 테스트")
    void streamTest() {
        String message = "Tell me a joke";
        Flux<ChatResponse> result = openAiChatModel.stream(new Prompt(new UserMessage(message)));

        StepVerifier.create(result)
                .thenConsumeWhile(response -> {
                    assertThat(response.getMetadata()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }


}
