package com.mishkin.nietzschenator.infrastructure.llm.gigachat.adapter;

import com.mishkin.nietzschenator.application.port.out.LlmClient;
import com.mishkin.nietzschenator.domain.model.EnrichmentResult;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.auth.GigaChatAuthService;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.request.GigaChatMessage;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response.GigaChatModelsResponse;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.request.GigaChatRequest;
import com.mishkin.nietzschenator.infrastructure.llm.gigachat.dto.response.GigaChatResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;

/**
 * @author a.mishkin
 */
@Service
public class GigaChatClientAdapter implements LlmClient {

    private final WebClient webClient;
    private final GigaChatAuthService auth;

    private static final String GIGACHAT_LITE_MODEL = "GigaChat-2";
    private static final String GIGACHAT_PRO_MODEL = "GigaChat-Pro";

    public GigaChatClientAdapter(WebClient.Builder builder, GigaChatAuthService auth,
            @Value("${gigachat.base-url}") String baseUrl) {
        this.auth = auth;
        this.webClient = builder
                .baseUrl(baseUrl) //https://gigachat.devices.sberbank.ru/api/v1
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    @CircuitBreaker(name = "gigachat", fallbackMethod = "fallback")
    @Retry(name = "gigachat")
    @TimeLimiter(name = "gigachat")
    public CompletionStage<EnrichmentResult> generate(String prompt) {

        GigaChatRequest request = new GigaChatRequest(
                GIGACHAT_LITE_MODEL,
                List.of(new GigaChatMessage("user", prompt)),
                false
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("RqUID", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GigaChatResponse.class)
                .map(r -> r.choices().get(0).message().content())
                .toFuture()
                .thenApply(text -> (EnrichmentResult) new EnrichmentResult.Success(text));

    }

    @SuppressWarnings("unused") // вызывается через reflection
    private CompletionStage<EnrichmentResult> fallback(String prompt, Throwable ex) {

        EnrichmentResult.Fallback.Reason reason = ex instanceof TimeoutException
                        ? EnrichmentResult.Fallback.Reason.TIMEOUT
                        : EnrichmentResult.Fallback.Reason.LLM_ERROR;

        return CompletableFuture.completedFuture((EnrichmentResult)
                new EnrichmentResult.Fallback(
                        "…молчание. Даже Заратустра не всегда находит слова.", reason)
        );
    }

    public GigaChatModelsResponse getModels() {
        return webClient.get()
                .uri("/models")
                .header("RqUID", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class)
                                .map(body -> new RuntimeException("GigaChat models error: " + body))
                )
                .bodyToMono(GigaChatModelsResponse.class)
                .block();
    }
}



