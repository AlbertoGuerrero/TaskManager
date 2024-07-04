package com.microservice.project.service.feign;

import com.microservice.commons.dto.TaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "task-service")
public interface TaskClient {
    @GetMapping("/tasks/project/{projectId}")
    List<TaskDTO> getTasksByProjectId(@PathVariable("projectId") Long projectId);
}
