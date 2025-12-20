package com.mishkin.nietzschenator.messaging.consumer;

import com.mishkin.nietzschenator.application.service.StatsEnrichmentService;
import com.mishkin.nietzschenator.messaging.event.StatsReadyEvent;
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
    public void consume(StatsReadyEvent event) {
        service.process(event);
    }
}

