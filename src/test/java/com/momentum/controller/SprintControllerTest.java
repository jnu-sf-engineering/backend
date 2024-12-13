package com.momentum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.domain.*;
import com.momentum.dto.SprintCreateRequest;
import com.momentum.dto.SprintUpdateRequest;
import com.momentum.repository.CardRepository;
import com.momentum.repository.ProjectRepository;
import com.momentum.repository.SprintRepository;
import com.momentum.repository.UserRepository;
import com.momentum.service.DiscordWebhookService;
import com.momentum.service.SprintService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SprintControllerTest {
    private static final String BASE_URL = "/v1/sprints";
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
    private SprintService sprintService;

    String principal;
    User user; Project project; Sprint sprint;
    String name; LocalDateTime start_date; LocalDateTime end_date;

    @BeforeEach
    @DisplayName("Sprint 테스트 세팅")
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
        name = "테스트 스프린트";
        start_date = LocalDateTime.now();
        end_date = LocalDateTime.of(2024,12,30,18,0);
    }

    @Test
    @DisplayName("Sprint 생성 테스트")
    void createSprint() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(
                SprintCreateRequest.builder()
                        .project_id(project.getId()).name(name).start_date(start_date).end_date(end_date)
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
        JsonNode root = objectMapper.readTree(jsonResponse);  // JSON 파싱후 sprint_id 추출
        Long sprint_id = root.path("response").path("sprint_id").asLong();

        Sprint s = sprintService.findById(sprint_id);
        assertThat(s.getProject().getId()).isEqualTo(project.getId());
        assertThat(s.getName()).isEqualTo(name);
        assertThat(s.getStart_date()).isEqualTo(start_date);
        assertThat(s.getEnd_date()).isEqualTo(end_date);
    }

    @Test
    @DisplayName("Sprint 조회 테스트")
    void readSprint() throws Exception {
        // given
        sprint = Sprint.builder().project(project).name(name).start_date(start_date).end_date(end_date)
                .build();
        sprintRepository.save(sprint);
        Long sprint_id = sprint.getId();

        String content1 = "첫 번째 테스트 카드 내용입니다.";
        String content2 = "두 번째 테스트 카드 내용입니다.";

        Set<String> participants = new HashSet<>();
        participants.add("연수"); participants.add("지송"); participants.add("경준");

        cardRepository.save(
                Card.builder().sprint(sprint)
                        .content(content1).status(Status.IN_PROGRESS).participants(participants)
                        .build()
        );
        cardRepository.save(
                Card.builder().sprint(sprint)
                        .content(content2).status(Status.TO_DO).participants(participants)
                        .build()
        );

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL+"/"+sprint_id)
                        .principal(() -> principal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response.name").value(name))
                .andExpect(jsonPath("$.response.start_date").exists())
                .andExpect(jsonPath("$.response.end_date").exists())
                .andExpect(jsonPath("$.response.cards.to_do[0].content").value(content2))
                .andExpect(jsonPath("$.response.cards.in_progress[0].content").value(content1));
    }

    @Test
    @DisplayName("Sprint 수정 테스트")
    void updateSprint() throws Exception {
        // given
        sprint = Sprint.builder().project(project).name(name).start_date(start_date).end_date(end_date)
                .build();
        sprintRepository.save(sprint);
        Long sprint_id = sprint.getId();

        String new_name = "수정된 테스트 스프린트";
        LocalDateTime new_start_date = LocalDateTime.now();
        LocalDateTime new_end_date = LocalDateTime.of(2024,12,30,18,0);

        String body = objectMapper.writeValueAsString(
                SprintUpdateRequest.builder()
                        .name(new_name).start_date(new_start_date).end_date(new_end_date)
                        .build()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL+"/"+sprint_id)
                        .principal(() -> principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").exists());

        // then
        Sprint s = sprintService.findById(sprint_id);
        assertThat(s.getName()).isEqualTo(new_name);
        assertThat(s.getStart_date()).isEqualTo(new_start_date);
        assertThat(s.getEnd_date()).isEqualTo(new_end_date);

    }

    @Test
    @DisplayName("Sprint 삭제 테스트")
    void deleteSprint() throws Exception {
        // given
        sprint = Sprint.builder().project(project).name(name).start_date(start_date).end_date(end_date)
                .build();
        sprintRepository.save(sprint);
        Long sprint_id = sprint.getId();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL+"/"+sprint_id)
                        .principal(() -> principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
