package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.security.MyUserDetails;
import net.noahk.takenlijst.services.ProjectService;
import net.noahk.takenlijst.services.TaskService;
import net.noahk.takenlijst.util.Util;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

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

    @GetMapping("/all/{id}")
    public ResponseEntity<Iterable<TaskDto>> getAll(@PathVariable long id) {
        return ResponseEntity.ok(service.getTasksByProject(id, false));
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<Iterable<TaskDto>> getTodo(@PathVariable long id) {
        return ResponseEntity.ok(service.getTasksByProject(id, true));
    }

    @GetMapping("/burndown/{id}/{start}/{end}")
    public ResponseEntity<Iterable<Integer>> getBurndown(@PathVariable long id, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return ResponseEntity.ok(service.getBurnDown(id, start, end, false));
    }

    @GetMapping("/burndown-predicted/{id}/{start}/{end}")
    public ResponseEntity<Iterable<Integer>> getPredictedBurndown(@PathVariable long id, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return ResponseEntity.ok(service.getBurnDown(id, start, end, true));
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
    public ResponseEntity<String> updateTask(@PathVariable long id, @Valid @RequestBody TaskDto task, @AuthenticationPrincipal MyUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        var taskDto = service.getTask(id);
        if (taskDto.isEmpty()) {
            return new ResponseEntity<>("Not found!", HttpStatus.NOT_FOUND);
        }

        if ((taskDto.get().assignedUser == null || !taskDto.get().assignedUser.equals(user.getUsername())) && !user.hasRole("TEAM_LEADER")) {
            return new ResponseEntity<>("You are not assigned to this task!", HttpStatus.UNAUTHORIZED);
        }

        boolean updated = service.update(id, task);

        if (!updated) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
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

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @AuthenticationPrincipal MyUserDetails user) {
        var taskDto = service.getTask(id);
        if (taskDto.isEmpty()) {
            return new ResponseEntity<>("Not found!", HttpStatus.NOT_FOUND);
        }

        if ((taskDto.get().assignedUser == null || !taskDto.get().assignedUser.equals(user.getUsername())) && !user.hasRole("TEAM_LEADER")) {
            return new ResponseEntity<>("You are not assigned to this task!", HttpStatus.UNAUTHORIZED);
        }

        service.delete(id);

        return ResponseEntity.ok("Attachment deleted!");
    }
}
