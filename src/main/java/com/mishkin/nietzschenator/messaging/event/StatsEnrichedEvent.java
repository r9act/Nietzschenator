package com.mishkin.nietzschenator.messaging.event;

/**
 * @author a.mishkin
 */
public record StatsEnrichedEvent(
        String correlationId,
        String text
) {}

