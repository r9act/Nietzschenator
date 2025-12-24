package com.mishkin.nietzschenator.application.service;

import com.mishkin.nietzschenator.application.port.out.LlmClient;
import com.mishkin.nietzschenator.application.prompt.PromptBuilder;
import com.mishkin.nietzschenator.domain.model.EnrichmentResult;
import com.mishkin.nietzschenator.messaging.event.envelope.EventEnvelope;
import com.mishkin.nietzschenator.messaging.event.outbound.StatsEnrichedEvent;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV1;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV2;
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
    private final PromptBuilder<StatsReadyV1> v1PromptBuilder;
    private final PromptBuilder<StatsReadyV2> v2PromptBuilder;

    public StatsEnrichmentService(LlmClient llmClient, StatsEnrichedProducer producer, PromptBuilder<StatsReadyV1> v1PromptBuilder, PromptBuilder<StatsReadyV2> v2PromptBuilder) {
        this.llmClient = llmClient;
        this.producer = producer;
        this.v1PromptBuilder = v1PromptBuilder;
        this.v2PromptBuilder = v2PromptBuilder;
    }

    public CompletionStage<Void> processV1(StatsReadyV1 event) {
        return generateAndPublish(
                event.correlationId(),
                event.platformUserHandle(),
                v1PromptBuilder.build(event)
        );
    }

    public CompletionStage<Void> processV2(StatsReadyV2 event) {
        return generateAndPublish(
                event.correlationId(),
                event.player().userHandle(),
                v2PromptBuilder.build(event)
        );
    }

    private CompletionStage<Void> generateAndPublish(String correlationId, String key, String prompt) {
        return llmClient.generate(prompt)
                .thenAccept(result -> {

                    String text = switch (result) {
                        case EnrichmentResult.Success s -> s.text();
                        case EnrichmentResult.Fallback f -> f.text();
                    };
                    producer.publish(
                            new EventEnvelope<>(
                                    "stats.enriched",
                                    1,
                                    new StatsEnrichedEvent(correlationId, text)
                            ),
                            key
                    );
                });
    }

    public CompletionStage<EnrichmentResult> processAndReturn(StatsReadyV1 event) {
        String prompt = TEST_PROMT.formatted(event.platformUserHandle());

        return llmClient.generate(prompt);
    }
}

