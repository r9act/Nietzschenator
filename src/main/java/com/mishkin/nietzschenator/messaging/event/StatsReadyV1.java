package com.mishkin.nietzschenator.messaging.event;

import java.time.Instant;

/**
 * @author a.mishkin
 */
public record StatsReadyV1(
        String correlationId,
        String platformUserHandle,
        Instant occurredAt
) {}