package com.microservice.task.service.service;

import com.microservice.commons.dto.TaskDTO;
import com.microservice.task.service.entity.Task;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAllTasks();
    TaskDTO getTaskById(Long id);
    TaskDTO createTask(Task task);
    TaskDTO updateTask(Long id, Task taskDetails);
    void deleteTask(Long id);
    List<TaskDTO> getTasksByProjectId(Long projectId);
    List<TaskDTO> getTasksByUserId(Long userId);
}
