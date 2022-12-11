package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.services.ProjectService;
import net.noahk.takenlijst.services.TaskService;
import net.noahk.takenlijst.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    private final TaskService service;
    private final ProjectService projectService;

    public TasksController(TaskService service, ProjectService projectService) {
        this.service = service;
        this.projectService = projectService;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<TaskDto>> index() {
        return ResponseEntity.ok(service.getTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTasks(@PathVariable long id) {
        var task = service.getTask(id);
        if (task.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(task.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable long id, @Valid @RequestBody TaskDto task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        boolean updated = service.update(id, task);

        if (!updated) {
            return new ResponseEntity<>("Not found!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/tasks/" + id).toUriString());

        return ResponseEntity.created(uri).body("Task updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody TaskDto task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (projectService.getProject(task.projectId).isEmpty()) {
            return new ResponseEntity<>("projectId: must exist!", HttpStatus.BAD_REQUEST);
        }

        Long id = service.create(task);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/tasks/" + id).toUriString());

        return ResponseEntity.created(uri).body("Task created!");
    }
}
