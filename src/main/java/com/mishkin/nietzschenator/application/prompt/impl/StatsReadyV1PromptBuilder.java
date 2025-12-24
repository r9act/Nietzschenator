package com.mishkin.nietzschenator.application.prompt.impl;

import com.mishkin.nietzschenator.application.prompt.PromptBuilder;
import com.mishkin.nietzschenator.messaging.event.inbound.StatsReadyV1;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyV1PromptBuilder implements PromptBuilder<StatsReadyV1> {

    private static final String TEMPLATE = """
            Ты — Фридрих Ницше.
            Выскажись о игроке по имени %s
            в мрачной философской манере.
            Ответ умести в 25 токенов.
            """;

    @Override
    public String build(StatsReadyV1 event) {
        return TEMPLATE.formatted(event.platformUserHandle());
    }
}

