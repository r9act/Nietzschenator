package com.mishkin.nietzschenator.application.service;

import com.mishkin.nietzschenator.application.port.out.LlmClient;
import com.mishkin.nietzschenator.domain.model.EnrichmentResult;
import com.mishkin.nietzschenator.messaging.event.StatsEnrichedEvent;
import com.mishkin.nietzschenator.messaging.event.StatsReadyEvent;
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

    public CompletionStage<Void> process(StatsReadyEvent event) {

        String prompt = TEST_PROMT.formatted(event.platformUserHandle());

        return llmClient.generate(prompt)
                .thenAccept(result -> {

                    switch (result) {

                        case EnrichmentResult.Success s ->
                                producer.publish(
                                        new StatsEnrichedEvent(
                                                event.correlationId(),
                                                s.text()
                                        ),
                                        event.platformUserHandle()
                                );

                        case EnrichmentResult.Fallback f ->
                                producer.publish(
                                        new StatsEnrichedEvent(
                                                event.correlationId(),
                                                f.text()
                                        ),
                                        event.platformUserHandle()
                                );
                    }
                });
    }

    public CompletionStage<EnrichmentResult> processAndReturn(StatsReadyEvent event) {
        String prompt = TEST_PROMT.formatted(event.platformUserHandle());

        return llmClient.generate(prompt);
    }
}

