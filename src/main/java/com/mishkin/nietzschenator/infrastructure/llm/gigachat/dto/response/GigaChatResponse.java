package com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response;

import java.util.List;

/**
 * @author a.mishkin
 */
public record GigaChatResponse(
        List<GigaChatChoice> choices
) {}