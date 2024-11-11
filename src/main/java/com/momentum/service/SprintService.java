package com.momentum.service;

import com.momentum.domain.Sprint;
import com.momentum.dto.SprintCreateRequest;
import com.momentum.dto.SprintUpdateRequest;
import com.momentum.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SprintService {
    private final SprintRepository sprintRepository;
    private final ProjectService projectService;

    @Transactional
    public Long save(SprintCreateRequest request) {
        Sprint sprint = Sprint.builder()
                .project(projectService.getOne(request.getProject_id()))
                .name(request.getName())
                .start_date(request.getStart_date())
                .end_date(request.getEnd_date())
                .build();

        sprintRepository.save(sprint);
        return sprint.getId();
    }

    @Transactional
    public Long update(Long id, SprintUpdateRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(("해당 스프린트가 없습니다. id=" + id)));

        return sprint.update(request.getName(), request.getStart_date(), request.getEnd_date());
    }

    @Transactional

    public void delete(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(("해당 스프린트가 없습니다. id=" + id)));

        sprintRepository.delete(sprint);
    }

    public Sprint getOne(Long id) {
        return sprintRepository.getById(id);
    }

    public Sprint findById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(("해당 스프린트가 없습니다. id=" + id)));
        return sprint;
    }
}
