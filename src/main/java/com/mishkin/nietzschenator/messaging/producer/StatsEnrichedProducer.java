package com.mishkin.nietzschenator.messaging.producer;

import com.mishkin.nietzschenator.messaging.event.StatsEnrichedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichedProducer {

    private final KafkaTemplate<String, StatsEnrichedEvent> kafka;

    public StatsEnrichedProducer(KafkaTemplate<String, StatsEnrichedEvent> kafka) {
        this.kafka = kafka;
    }

    public void publish(StatsEnrichedEvent event, String key) {
        kafka.send("stats.enriched", key, event);
    }
}
