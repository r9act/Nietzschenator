package com.mishkin.nietzschenator.application.promptBuilder.impl;

import com.mishkin.nietzschenator.application.promptBuilder.PromptBuilder;
import com.mishkin.nietzschenator.messaging.event.StatsReadyV1;
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
            Ответ умести в 75 токенов.
            """;

    @Override
    public String build(StatsReadyV1 event) {
        return TEMPLATE.formatted(event.userHandle());
    }
}

