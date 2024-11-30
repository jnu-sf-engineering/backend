package com.momentum.service;

import com.momentum.domain.Project;
import com.momentum.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(("해당 프로젝트가 없습니다. id=" + id)));
        return project;
    }
}
