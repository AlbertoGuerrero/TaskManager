package com.microservice.project.service.service;

import com.microservice.common.dto.ProjectDTO;
import com.microservice.common.dto.TaskDTO;
import com.microservice.project.service.entity.Project;
import com.microservice.project.service.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        return project != null ? convertToDTO(project) : null;
    }

    @Override
    public ProjectDTO createProject(Project project) {
        project = projectRepository.save(project);
        return convertToDTO(project);
    }

    @Override
    public ProjectDTO updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null) {
            project.setId(projectDetails.getId());
            project.setName(projectDetails.getName());
            project.setDescription(projectDetails.getDescription());
            project = projectRepository.save(project);
            return convertToDTO(project);
        }
        return null;
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        return projectDTO;
    }

    private Project convertToEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return project;
    }
}
