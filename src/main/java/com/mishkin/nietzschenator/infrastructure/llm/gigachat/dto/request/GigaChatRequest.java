package com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.request;

import java.util.List;

/**
 * @author a.mishkin
 */
public record GigaChatRequest(
        String model,
        List<GigaChatMessage> messages,
        boolean stream
) {}
