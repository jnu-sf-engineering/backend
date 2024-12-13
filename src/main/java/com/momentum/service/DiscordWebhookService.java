package com.momentum.service;

import com.momentum.dto.DiscordWebhookMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DiscordWebhookService {

    public void callEvent(String webhookUrl, DiscordWebhookMessage message) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<DiscordWebhookMessage> entity = new HttpEntity<>(message, headers);

        send(webhookUrl, entity);
    }

    public void send(String webhookUrl, HttpEntity<DiscordWebhookMessage> entity) {
        RestTemplate rt = new RestTemplate();

        try {
            ResponseEntity<String> response = rt.exchange(
                    webhookUrl,  // 요청할 서버 주소
                    HttpMethod.POST,
                    entity,
                    String.class
            );

        } catch (Exception e) {
            log.error("Discord Webhook Error : {}.", e.getMessage());
        }
    }
}
