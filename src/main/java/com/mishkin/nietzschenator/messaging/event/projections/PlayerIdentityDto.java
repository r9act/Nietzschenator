package com.mishkin.nietzschenator.messaging.event.projections;

/**
 * @author a.mishkin
 */
public record PlayerIdentityDto(
        String platform,
        String userHandle
) {}

