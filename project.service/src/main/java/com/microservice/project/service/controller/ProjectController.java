package com.microservice.project.service.controller;

import com.microservice.common.dto.ProjectDTO;
import com.microservice.common.dto.TaskDTO;
import com.microservice.project.service.feign.TaskClient;
import com.microservice.project.service.entity.Project;
import com.microservice.project.service.service.ProjectService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskClient taskClient;

    @GetMapping
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public ProjectDTO createProject(@RequestBody Project project) {
        try {
            return projectService.createProject(project);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ProjectDTO updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        return projectService.updateProject(id, projectDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @CircuitBreaker(name="taskClientTasksByProjectId", fallbackMethod = "fallbackGetTasksByProjectId")
    @GetMapping("/{id}/tasks")
    public List<TaskDTO> getTasksByProjectId(@PathVariable Long id) {
        return taskClient.getTasksByProjectId(id);
    }

    public List<TaskDTO> fallbackGetTasksByProjectId(Long projectId, Throwable throwable) {
        System.out.println("Error to get tasks from projectId: " + projectId
                + " fallback reason: " + throwable.getMessage());
        return Collections.emptyList();
    }
}
