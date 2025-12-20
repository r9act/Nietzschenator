package com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response;

import java.util.List;

/**
 * @author a.mishkin
 */
public record GigaChatModelsResponse(
        List<ModelInfo> data
) {
    public record ModelInfo(
            String id,
            String object
    ) {}
}
