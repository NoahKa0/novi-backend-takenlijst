package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.ProjectDto;
import net.noahk.takenlijst.services.ProjectService;
import net.noahk.takenlijst.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/projects")
public class ProjectsController {

    private final ProjectService service;

    public ProjectsController(ProjectService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<ProjectDto>> index() {
        return ResponseEntity.ok(service.getProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable long id) {
        var project = service.getProject(id);
        if (project.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(project.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable long id, @Valid @RequestBody ProjectDto project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        boolean updated = service.update(id, project);

        if (!updated) {
            return new ResponseEntity<>("Not found!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/projects/" + id).toUriString());

        return ResponseEntity.created(uri).body("Project updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody ProjectDto project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        Long id = service.create(project);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/projects/" + id).toUriString());

        return ResponseEntity.created(uri).body("Project created!");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        if (service.getProject(id).isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        service.delete(id);

        return ResponseEntity.ok("Project deleted!");
    }
}
