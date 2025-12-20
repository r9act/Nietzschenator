package com.mishkin.nietzschenator.application.port.out;

import com.mishkin.nietzschenator.domain.model.EnrichmentResult;

import java.util.concurrent.CompletionStage;

public interface LlmClient {
    CompletionStage<EnrichmentResult> generate(String prompt);
}
