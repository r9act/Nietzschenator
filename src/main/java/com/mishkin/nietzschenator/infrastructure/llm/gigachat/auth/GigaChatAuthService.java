package com.mishkin.nietzschenator.infrastructure.llm.gigachat.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author a.mishkin
 */
@Service
public class GigaChatAuthService {

    private final WebClient webClient;

    private String token;
    private Instant expiresAt;
    private String scope;

    public GigaChatAuthService(
            WebClient.Builder builder,
            @Value("${gigachat.auth-url}") String authUrl,
            @Value("${gigachat.authorization-key}") String authorizationKey,
            @Value("${gigachat.scope}") String scope
    ) {
        this.webClient = builder
                .baseUrl(authUrl) // https://ngw.devices.sberbank.ru:9443
                .defaultHeaders(h -> {
                    h.set(HttpHeaders.AUTHORIZATION, "Basic " + authorizationKey);
                    h.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();
        this.scope = scope;
    }

    /** lazy получение токена */
    public synchronized String getToken() {
        if (token == null || expiresAt == null) {
            fetchToken();
            return token;
        }

        if (Instant.now().isAfter(expiresAt.minusSeconds(30))) {
            fetchToken();
        }

        return token;
    }

    private void fetchToken() {
        JsonNode json = webClient.post()
                .uri("/api/v2/oauth")
                .header("RqUID", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("scope", scope))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (json == null || json.get("access_token") == null) {
            throw new IllegalStateException("No access_token in response: " + json);
        }

        /**
         * {
         *   "access_token": "xxx",
         *   "expires_in": 1800
         * }
         */
        this.token = json.get("access_token").asText();

        JsonNode expiresNode = json.get("expires_in");
        long expiresIn = (expiresNode != null && expiresNode.isNumber())
                ? expiresNode.asLong() : 1800;

        this.expiresAt = Instant.now().plusSeconds(expiresIn);
    }
}



