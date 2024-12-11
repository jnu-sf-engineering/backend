package com.momentum.service;

import com.momentum.domain.Project;
import com.momentum.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("해당 프로젝트가 없습니다. id=" + id)));
    }
}
