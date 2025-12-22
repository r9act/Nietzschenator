package com.mishkin.nietzschenator.domain.model;

/**
 * @author a.mishkin
 */
public sealed interface EnrichmentResult permits EnrichmentResult.Success, EnrichmentResult.Fallback {

    record Success(String text) implements EnrichmentResult {}

    record Fallback(String text, Reason reason) implements EnrichmentResult {}

    enum Reason {
        TIMEOUT,
        CIRCUIT_OPEN,
        LLM_ERROR
    }
}

