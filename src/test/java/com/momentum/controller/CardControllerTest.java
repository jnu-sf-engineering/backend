package com.momentum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.domain.*;
import com.momentum.dto.CardCreateRequest;
import com.momentum.dto.CardUpdateRequest;
import com.momentum.repository.CardRepository;
import com.momentum.repository.ProjectRepository;
import com.momentum.repository.SprintRepository;
import com.momentum.repository.UserRepository;
import com.momentum.service.CardService;
import com.momentum.service.DiscordWebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CardControllerTest {
    private static final String BASE_URL = "/v1/cards";
    private MockMvc mockMvc;  // 수동 초기화

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SecurityFilterChain securityFilterChain;  // 기존 Filter 대체 (인증 요구 안함)
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private DiscordWebhookService discordWebhookService;  // 가짜 서비스 (Webhook 전송 안함)
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    SprintRepository sprintRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardService cardService;

    String principal;
    User user; Project project; Sprint sprint; Card card;
    String content; Set<String> participants; Status status;

    @BeforeEach
    @DisplayName("Card 테스트 세팅")
    public void setup() {
        // MockMvc 세팅
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // user 세팅
        user = User.builder().email("test@jnu.ac.kr").password("jnu-sf-engineering").nickname("test")
                .discord("https://discord.com/api/webhooks/1317074642715938897/zZXTbwHvui-zisDMDQ_fowVs3CxyfHh74QETzhpodv_RRYv6AjX5_KcL8Loc7oOcqwNR")
                .build();
        userRepository.save(user);
        // 사용자 세팅
        principal = String.valueOf(user.getId());

        // project 세팅
        project = Project.builder().user(user).name("테스트 프로젝트").manager("admin")
                .build();
        projectRepository.save(project);

        // sprint 세팅
        sprint = Sprint.builder().project(project).name("테스트 스프린트")
                .start_date(LocalDateTime.now()).end_date(LocalDateTime.of(2024,12,30,18,0))
                .build();
        sprintRepository.save(sprint);

        // card 세팅
        content = "테스트 카드 내용입니다.";
        participants = new HashSet<>();
        participants.add("연수");
        participants.add("지송");
        participants.add("경준");
        status = Status.IN_PROGRESS;
    }

    @Test
    @DisplayName("Card 생성 테스트")
    void createCard() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(
                CardCreateRequest.builder()
                        .sprint_id(sprint.getId()).content(content).participants(participants).status(status)
                        .build()
        );

        // when
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .principal(() -> principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        // then
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);  // JSON 파싱후 card_id 추출
        Long card_id = root.path("response").path("card_id").asLong();

        Card c = cardService.findById(card_id);
        assertThat(c.getSprint().getId()).isEqualTo(sprint.getId());
        assertThat(c.getContent()).isEqualTo(content);
        assertThat(c.getParticipants()).isEqualTo(participants);
        assertThat(c.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Card 조회 테스트")
    void readCard() throws Exception {
        // given
        card = Card.builder().sprint(sprint).content(content).participants(participants).status(status)
                .build();
        cardRepository.save(card);
        Long card_id = card.getId();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL+"/"+card_id)
                        .principal(() -> principal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.content").value(content))
                .andExpect(jsonPath("$.response.participants").exists())
                .andExpect(jsonPath("$.response.status").value(status.getStatusString()));
    }

    @Test
    @DisplayName("Card 수정 테스트")
    void updateCard() throws Exception {
        // given
        card = Card.builder().sprint(sprint).content(content).participants(participants).status(status)
                .build();
        cardRepository.save(card);
        Long card_id = card.getId();

        String new_content = "수정된 테스트 카드";
        HashSet<String> new_participants = new HashSet<>();
        new_participants.add("서윤");
        new_participants.add("규민");

        Status new_status = Status.TO_DO;

        String body = objectMapper.writeValueAsString(
                CardUpdateRequest.builder()
                        .content(new_content).participants(new_participants).status(new_status)
                        .build()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL+"/"+card_id)
                        .principal(() -> principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").exists());

        // then
        Card c = cardService.findById(card_id);
        assertThat(c.getContent()).isEqualTo(new_content);
        assertThat(c.getParticipants()).isEqualTo(new_participants);
        assertThat(c.getStatus()).isEqualTo(new_status);
    }

    @Test
    @DisplayName("Card 삭제 테스트")
    void deleteCard() throws Exception {
        // given
        card = Card.builder().sprint(sprint).content(content).participants(participants).status(status)
                .build();
        cardRepository.save(card);
        Long card_id = card.getId();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL+"/"+card_id)
                        .principal(() -> principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Card 이동 테스트")
    void moveCard() throws Exception {
        // given
        card = Card.builder().sprint(sprint).content(content).participants(participants).status(status)
                .build();
        cardRepository.save(card);
        Long card_id = card.getId();

        Status new_status = Status.DONE;

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL+"/"+card_id+"/move")
                        .principal(() -> principal)
                        .param("status", new_status.getStatusString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.prev_status").value(status.getStatusString()))
                .andExpect(jsonPath("$.response.status").value(new_status.getStatusString()));
    }
}
