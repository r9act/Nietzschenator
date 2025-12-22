package com.mishkin.nietzschenator.messaging.event;

import com.mishkin.nietzschenator.messaging.event.projections.CareerRankDto;
import com.mishkin.nietzschenator.messaging.event.projections.PlayerIdentityDto;
import com.mishkin.nietzschenator.messaging.event.projections.RedSecModeStatsDto;

import java.time.Instant;
import java.util.Map;

/**
 * @author a.mishkin
 */
public record StatsReadyV2(
        String correlationId,

        PlayerIdentityDto player,

        CareerRankDto careerRank,

        RedSecModeStatsDto total,

        Map<String, RedSecModeStatsDto> modes,

        Instant fetchedAt,

        String source
) {}

