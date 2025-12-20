package com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response;

import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.request.GigaChatMessage;

/**
 * @author a.mishkin
 */
public record GigaChatChoice(
        GigaChatMessage message
) {}
