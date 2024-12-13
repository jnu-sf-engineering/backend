package com.momentum.controller;

import com.momentum.domain.Card;
import com.momentum.domain.Status;
import com.momentum.domain.User;
import com.momentum.dto.*;
import com.momentum.global.CommonResponse;
import com.momentum.global.ErrorCode;
import com.momentum.service.CardService;
import com.momentum.service.DiscordWebhookService;
import com.momentum.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/v1/cards")
@RestController
public class CardController {
    private final CardService cardService;
    private final SprintService sprintService;
    private final DiscordWebhookService discordWebhookService;

    @PostMapping()
    public ResponseEntity<CommonResponse<Object>> create(@Valid @RequestBody CardCreateRequest request, BindingResult bindingResult,
                                                         Principal principal) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            String errorMessage = "필드 데이터 누락입니다.("+extractErrorMessages(bindingResult)+")";
            return responseEntityWithError(4001, errorMessage, HttpStatus.BAD_REQUEST);
        }

        User user = sprintService.findById(request.getSprint_id()).getProject().getUser();
        permissionAuth(principal, user.getId());

        CardCreateResponse response = CardCreateResponse
                .from(cardService.save(request));

        // Discord Webhook 전송
        discordWebhookCall(user.getDiscord(),
                "할일 작성 봇", "할일이 등록되었습니다.",
                request.getContent(), request.getParticipants().toString());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @GetMapping("/{card_id}")
    public ResponseEntity<CommonResponse<Object>> read(@PathVariable("card_id") Long card_id,
                                                       Principal principal) throws IllegalAccessException {
        Long user_id = cardService.findById(card_id).getSprint().getProject().getUser().getId();
        permissionAuth(principal, user_id);

        CardFindResponse response = CardFindResponse
                .from(cardService.findById(card_id));
        return ResponseEntity.ok().body(CommonResponse.success(response));
    }

    @PutMapping("/{card_id}")
    public ResponseEntity<CommonResponse<Object>> update(@PathVariable("card_id") Long card_id,
                                                         @Valid @RequestBody CardUpdateRequest request, BindingResult bindingResult,
                                                         Principal principal) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            String errorMessage = "필드 데이터 누락입니다.("+extractErrorMessages(bindingResult)+")";
            return responseEntityWithError(4001, errorMessage, HttpStatus.BAD_REQUEST);
        }

        User user = cardService.findById(card_id).getSprint().getProject().getUser();
        permissionAuth(principal, user.getId());

        CardCreateResponse response = CardCreateResponse
                .from(cardService.update(card_id, request));

        // Discord Webhook 전송
        if (request.getStatus() == Status.DONE) {
            discordWebhookCall(user.getDiscord(),
                    "할일 작성 봇", "할일이 완료되었습니다.",
                    request.getContent(), request.getParticipants().toString());
        }

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @DeleteMapping("/{card_id}")
    public ResponseEntity<CommonResponse<Object>> delete(@PathVariable("card_id") Long card_id,
                                                         Principal principal) throws IllegalAccessException {
        Long user_id = cardService.findById(card_id).getSprint().getProject().getUser().getId();
        permissionAuth(principal, user_id);

        cardService.delete(card_id);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @PostMapping("/{card_id}/move")
    public ResponseEntity<CommonResponse<Object>> move(@PathVariable("card_id") Long card_id,
                                                       @RequestParam("status") Status status,
                                                       Principal principal) throws IllegalAccessException {
        Card card = cardService.findById(card_id);
        Status prev_status = card.getStatus();

        User user = card.getSprint().getProject().getUser();
        permissionAuth(principal, user.getId());

        cardService.move(card_id, status);
        CardMoveResponse response = CardMoveResponse.from(card, prev_status);

        // Discord Webhook 전송
        if (status == Status.DONE) {
            discordWebhookCall(user.getDiscord(),
                    "할일 작성 봇", "할일이 완료되었습니다.",
                    card.getContent(), card.getParticipants().toString());
        }

        return ResponseEntity.ok(CommonResponse.success(response));
    }


    /*
     * Helper Functions Down Here.
     * */

    // 접근 권한 인증
    private void permissionAuth(Principal principal, Long user_id) throws IllegalAccessException {
        if (!principal.getName().equals(String.valueOf(user_id))) {
            throw new IllegalAccessException("요청 권한이 없습니다.");
        }
    }

    // error 내용을 담은 ResponseEntity 생성
    private ResponseEntity<CommonResponse<Object>> responseEntityWithError(int code, String errorMessage, HttpStatus status) {
        return ResponseEntity.status(status).body(
                CommonResponse.error(ErrorCode.builder()
                        .code(code).reason(errorMessage).status(status.value()).build())
        );
    }

    // 에러 메시지를 추출하는 유틸리티 함수
    private String extractErrorMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    // Discord Webhook 전송
    void discordWebhookCall(String webhookUrl, String username, String content, String title, String description) {
        DiscordWebhookMessage.Embed embed = new DiscordWebhookMessage.Embed();
        embed.setTitle(title);
        embed.setDescription(description);

        discordWebhookService.callEvent(webhookUrl,
                DiscordWebhookMessage.builder()
                        .username(username)
                        .content(content)
                        .embeds(List.of(embed))
                        .build()
        );
    }
}
