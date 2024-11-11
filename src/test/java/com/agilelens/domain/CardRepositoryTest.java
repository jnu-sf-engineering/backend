package com.agilelens.domain;

import com.agilelens.repository.CardRepository;
import com.agilelens.repository.ProjectRepository;
import com.agilelens.repository.SprintRepository;
import com.agilelens.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Transactional  // roll back
@SpringBootTest
public class CardRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    SprintRepository sprintRepository;
    @Autowired
    CardRepository cardRepository;

    User user; Project project; Sprint sprint;
    Card card;
    String content; Set<String> participants; Status status;

    @Before
    public void init() {
        // user
        user = User.builder().email("test@jnu.ac.kr").password("jnu-sf-engineering")
                .build();
        userRepository.save(user);

        // project
        project = Project.builder().user(user).name("테스트 프로젝트").manager("admin")
                .build();
        projectRepository.save(project);

        // sprint
        LocalDateTime start_date = LocalDateTime.now();
        LocalDateTime end_date = LocalDateTime.now();

        sprint = Sprint.builder().project(project).name("테스트 스프린트").start_date(start_date).end_date(end_date)
                              .build();
        sprintRepository.save(sprint);

        // card
        content = "테스트 카드 내용입니다.";
        participants = new HashSet<>();
        participants.add("연수");
        participants.add("지송");
        participants.add("경준");
        status = Status.TO_DO;
        card = Card.builder().content(content).participants(participants).status(status).sprint(sprint)
                .build();
        cardRepository.save(card);
    }

    @Test
    public void 카드저장_읽기() {
        // given
        // when
        List<Card> cardList = cardRepository.findAll();
        Card card = cardList.get(0);

        // then
        assertThat(card.getContent()).isEqualTo(content);
        assertThat(card.getStatus()).isEqualTo(status);
    }

    @Test
    public void 카드수정() {
        // given
        String updated_content = "업데이트 된 테스트 카드 내용입니다.";
        Set<String> updated_participants = new HashSet<>();
        updated_participants.add("규민");
        updated_participants.add("서윤");
        Status updated_status = Status.IN_PROGRESS;

        // when
        Long id = card.update(updated_content, updated_participants, updated_status);

        // then
        Optional<Card> oc = cardRepository.findById(id);
        Card updated_card = new Card();
        if (oc.isPresent())
            updated_card = oc.get();

        assertThat(updated_card.getContent()).isEqualTo(updated_content);
        assertThat(updated_card.getStatus()).isEqualTo(updated_status);
    }

    @Test
    public void 카드삭제() {
        // given
        // when
        List<Card> cardList = cardRepository.findAll();
        Card card = cardList.get(0);
        Long id = card.getId();

        cardRepository.delete(card);
        Optional<Card> oc = cardRepository.findById(id);

        // then
        assertThat(oc.isEmpty()).isEqualTo(true);
    }
}
