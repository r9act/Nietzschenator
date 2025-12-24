package com.mishkin.nietzschenator.application.prompt.impl;

import com.mishkin.nietzschenator.application.prompt.PromptBuilder;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV2;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyV2PromptBuilder implements PromptBuilder<StatsReadyV2> {

    @Override
    public String build(StatsReadyV2 event) {

        var total = event.total();
        var rank = event.careerRank();
        var player = event.player();

        return """
            Ты — Фридрих Ницше.
            Перед тобой воин по имени %s.

            Его путь:
            - Ранг: %d
            - Матчей сыграно: %d
            - Побед: %d
            - K/D: %.2f

            Истолкуй его стиль игры и характер,
            как философ трагического героизма.
            Ответ умести в 75 токенов.
            """
                .formatted(
                        player.userHandle(),
                        rank.rank(),
                        total.matchesPlayed(),
                        total.matchesWon(),
                        total.kd()
                );
    }
}
