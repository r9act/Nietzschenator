package com.mishkin.nietzschenator.messaging.producer;

import com.mishkin.nietzschenator.messaging.event.envelope.EventEnvelope;
import com.mishkin.nietzschenator.messaging.event.outbound.StatsEnrichedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichedProducer {

    private final KafkaTemplate<String, EventEnvelope<StatsEnrichedEvent>> kafka;

    public StatsEnrichedProducer(KafkaTemplate<String, EventEnvelope<StatsEnrichedEvent>> kafka) {
        this.kafka = kafka;
    }

    public void publish(EventEnvelope<StatsEnrichedEvent> event, String key) {
        kafka.send("stats.enriched", key, event);
    }
}
