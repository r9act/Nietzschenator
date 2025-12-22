package com.mishkin.nietzschenator.messaging.event;

/**
 * @author a.mishkin
 */
public record EventEnvelope<T>(
        String type,
        int version,
        T payload
) {}