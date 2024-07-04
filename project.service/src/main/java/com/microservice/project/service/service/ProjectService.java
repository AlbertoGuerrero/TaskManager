package com.microservice.project.service.service;

import com.microservice.commons.dto.ProjectDTO;
import com.microservice.project.service.entity.Project;

import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects();
    ProjectDTO getProjectById(Long id);
    ProjectDTO createProject(Project project);
    ProjectDTO updateProject(Long id, Project projectDetails);
    void deleteProject(Long id);
}
