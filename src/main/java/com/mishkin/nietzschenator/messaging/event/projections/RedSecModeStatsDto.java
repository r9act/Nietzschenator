package com.mishkin.nietzschenator.messaging.event.projections;

import java.time.Duration;

/**
 * @author a.mishkin
 */
public record RedSecModeStatsDto(
        int matchesPlayed,
        int matchesWon,
        double winRate,
        int kills,
        int deaths,
        double kd,
        Duration timePlayed
) {}
