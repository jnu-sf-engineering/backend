package com.momentum.service;

import com.momentum.domain.Sprint;
import com.momentum.dto.SprintCreateRequest;
import com.momentum.dto.SprintUpdateRequest;
import com.momentum.repository.SprintRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .project(projectService.findById(request.getProject_id()))
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
                .orElseThrow(() -> new EntityNotFoundException("요청한 스프린트가 없습니다. id=" + id));

        return sprint.update(request.getName(), request.getStart_date(), request.getEnd_date());
    }

    @Transactional

    public void delete(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("요청한 스프린트가 없습니다. id=" + id)));

        sprintRepository.delete(sprint);
    }

    public Sprint findById(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("요청한 스프린트가 없습니다. id=" + id)));
    }

    public boolean existsByProjectIdAndName(Long project_id, String name) {
        return sprintRepository.existsByProjectIdAndName(project_id, name);
    }
}
