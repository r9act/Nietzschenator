package com.mishkin.nietzschenator.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishkin.nietzschenator.application.service.StatsEnrichmentService;
import com.mishkin.nietzschenator.messaging.event.envelope.EventEnvelope;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV1;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV2;
import com.mishkin.nietzschenator.messaging.idempotancy.DedupStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyEventListener {

    private final StatsEnrichmentService service;
    private final ObjectMapper objectMapper;
    private final DedupStore dedupStore;

    public StatsReadyEventListener(StatsEnrichmentService service, ObjectMapper objectMapper, DedupStore dedupStore) {
        this.service = service;
        this.objectMapper = objectMapper;
        this.dedupStore = dedupStore;
    }

    @KafkaListener(topics = "stats.ready", groupId = "nietzschenator")
    public void consume(EventEnvelope<?> envelope) {
        switch (envelope.version()) {

            case 1 -> {
                StatsReadyV1 v1 = objectMapper.convertValue(
                        envelope.payload(),
                        StatsReadyV1.class
                );
                if (!dedupStore.claim(v1.correlationId())) {
                    return;
                }
                service.processV1(v1);
            }

            case 2 -> {
                StatsReadyV2 v2 = objectMapper.convertValue(
                        envelope.payload(),
                        StatsReadyV2.class
                );
                if (!dedupStore.claim(v2.correlationId())) {
                    return;
                }
                service.processV2(v2);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported stats.ready version " + envelope.version()
            );
        }
    }
}

