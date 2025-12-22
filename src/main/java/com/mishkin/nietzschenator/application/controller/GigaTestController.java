package com.mishkin.nietzschenator.application.controller;

import com.mishkin.nietzschenator.application.service.StatsEnrichmentService;
import com.mishkin.nietzschenator.domain.model.EnrichmentResult;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response.GigaChatModelsResponse;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * @author a.mishkin
 */
@RestController
@RequestMapping("/api/giga")
public class GigaTestController {

    private final StatsEnrichmentService enrichmentService;

    public GigaTestController(StatsEnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @GetMapping("/{playerName}")
    public ResponseEntity<Void> test(@PathVariable String playerName) {

        enrichmentService.processV1(
                new StatsReadyV1(
                        UUID.randomUUID().toString(),
                        playerName,
                        Instant.now()
                )
        );

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{playerName}/sync")
    public CompletionStage<ResponseEntity<String>> testSync(@PathVariable String playerName) {

        return enrichmentService.processAndReturn(
                new StatsReadyV1(
                        "test",
                        playerName,
                        Instant.now()
                )
        ).thenApply(result -> {

            String text = switch (result) {
                case EnrichmentResult.Success s -> s.text();
                case EnrichmentResult.Fallback f -> f.text();
            };

            return ResponseEntity.ok(text);
        });
    }

    @GetMapping("/models")
    public GigaChatModelsResponse models() {
        return null;
    }
}
