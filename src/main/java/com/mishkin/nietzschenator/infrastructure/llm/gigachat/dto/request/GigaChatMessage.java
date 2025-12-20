package com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.request;

/**
 * @author a.mishkin
 */
public record GigaChatMessage(
        String role,
        String content
) {}
