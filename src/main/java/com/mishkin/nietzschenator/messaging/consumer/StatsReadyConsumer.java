package com.mishkin.nietzschenator.messaging.consumer;

import com.mishkin.nietzschenator.application.service.StatsEnrichmentService;
import com.mishkin.nietzschenator.messaging.event.EventEnvelope;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV1;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyConsumer {

    private final StatsEnrichmentService service;

    public StatsReadyConsumer(StatsEnrichmentService service) {
        this.service = service;
    }

    @KafkaListener(topics = "stats.ready", groupId = "nietzschenator")
    public void consume(EventEnvelope<?> envelope) {
        switch (envelope.version()) {

            case 1 -> service.processV1((StatsReadyV1) envelope.payload());

            case 2 -> service.processV2((StatsReadyV2) envelope.payload());

            default -> throw new IllegalArgumentException(
                    "Unsupported stats.ready version " + envelope.version()
            );
        }
    }
}

