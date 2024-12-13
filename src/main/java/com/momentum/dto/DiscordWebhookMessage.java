package com.momentum.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class DiscordWebhookMessage {
    private String username;
    private String content;
    private boolean tts = false; // 텍스트 음성 변환
    private List<Embed> embeds = new ArrayList<>();

    @Builder
    public DiscordWebhookMessage(String username, String content, List<Embed> embeds) {
        this.username = username;
        this.content = content;
        this.embeds = embeds;
    }

    @Data
    public static class Embed {
        private String title;
        private String description;
    }
}
