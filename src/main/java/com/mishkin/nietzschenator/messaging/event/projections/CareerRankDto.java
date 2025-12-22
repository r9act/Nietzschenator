package com.mishkin.nietzschenator.messaging.event.projections;

/**
 * @author a.mishkin
 */
public record CareerRankDto(
        int rank,
        String name,
        String imageUrl
) {}

