package com.mishkin.nietzschenator.application.service;

import com.mishkin.nietzschenator.application.port.out.LlmClient;
import com.mishkin.nietzschenator.domain.model.EnrichmentResult;
import com.mishkin.nietzschenator.messaging.event.StatsEnrichedEvent;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV1;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV2;
import com.mishkin.nietzschenator.messaging.producer.StatsEnrichedProducer;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletionStage;

/**
 * @author a.mishkin
 */
@Service
public class StatsEnrichmentService {

    public static final String BASE_PROMT = """
            Ты — Фридрих Ницше.
            Выскажись о игроке по имени %s в мрачной философской манере. Ответ умести в 75 токенов
            """;

    public final String TEST_PROMT = "Say hi to %s.";

    private final LlmClient llmClient;
    private final StatsEnrichedProducer producer;

    public StatsEnrichmentService(LlmClient llmClient, StatsEnrichedProducer producer) {
        this.llmClient = llmClient;
        this.producer = producer;
    }

    public CompletionStage<Void> processV1(StatsReadyV1 event) {

        String prompt = TEST_PROMT.formatted(event.platformUserHandle());

        return generateAndPublish(event.correlationId(), event.platformUserHandle(), prompt);
    }

    public CompletionStage<Void> processV2(StatsReadyV2 event) {

        String prompt = promptFromStats(event);

        return generateAndPublish(event.correlationId(), event.player().userHandle(), prompt);
    }

    private String promptFromStats(StatsReadyV2 event) {
        // позже будет отдельный PromptBuilder
        return """
                Ты — Фридрих Ницше.
                Игрок %s имеет ранг %d.
                Всего матчей: %d, K/D: %.2f.
                Выскажись философски.
                """.formatted(
                event.player().userHandle(),
                event.careerRank().rank(),
                event.total().matchesPlayed(),
                event.total().kd()
        );
    }


    private CompletionStage<Void> generateAndPublish(String correlationId, String key, String prompt) {
        return llmClient.generate(prompt)
                .thenAccept(result -> {

                    String text = switch (result) {
                        case EnrichmentResult.Success s -> s.text();
                        case EnrichmentResult.Fallback f -> f.text();
                    };
                    producer.publish(new StatsEnrichedEvent(correlationId, text), key);
                });
    }

    public CompletionStage<EnrichmentResult> processAndReturn(StatsReadyV1 event) {
        String prompt = TEST_PROMT.formatted(event.platformUserHandle());

        return llmClient.generate(prompt);
    }
}

