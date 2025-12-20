package com.mishkin.nietzschenator.messaging.event;

import java.time.Instant;

/**
 * @author a.mishkin
 */
public record StatsReadyEvent(
        String correlationId,
        String platformUserHandle,
        Instant occurredAt
) {}