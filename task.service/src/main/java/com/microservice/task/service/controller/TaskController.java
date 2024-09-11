package com.microservice.task.service.controller;

import com.microservice.commons.dto.TaskDTO;
import com.microservice.commons.dto.UserDTO;
import com.microservice.commons.kafka.Notification;
import com.microservice.task.service.entity.Task;
import com.microservice.task.service.feign.AuthClient;
import com.microservice.task.service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private KafkaTemplate<String, Notification> kafkaTemplate;
    @Autowired
    private AuthClient authClient;

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public TaskDTO createTask(@RequestBody Task task) {
        try {
            TaskDTO taskDTO = taskService.createTask(task);
            if (taskDTO.getUserId() != null) {
                UserDTO userDTO = authClient.getByUserId(task.getUserId());
                Notification notification = Notification.builder()
                        .subject("Asignación de la Tarea: " + taskDTO.getTitle())
                        .mesage("Se le ha asignado la tarea con ID: " + taskDTO.getId())
                        .email(userDTO.getEmail()).build();
                kafkaTemplate.send("send_mail", notification);
            }
            return taskDTO;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskService.updateTask(id, taskDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskDTO> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<TaskDTO> getTasksByUserId(@PathVariable Long userId){
        return taskService.getTasksByUserId(userId);
    }
}
