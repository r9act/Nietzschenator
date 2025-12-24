package com.mishkin.nietzschenator.messaging.event.outbound;

/**
 * @author a.mishkin
 */
public record StatsEnrichedEvent(
        String correlationId,
        String text
) {}

