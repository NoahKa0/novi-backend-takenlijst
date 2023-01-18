package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.ProjectDto;
import net.noahk.takenlijst.dtos.ProjectMemberDto;
import net.noahk.takenlijst.exceptions.UnmetPreconditionException;
import net.noahk.takenlijst.security.MyUserDetails;
import net.noahk.takenlijst.services.ProjectService;
import net.noahk.takenlijst.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Object> getProject(@PathVariable long id, @AuthenticationPrincipal MyUserDetails user) {
        var project = service.getProject(id);
        if (project.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (!service.isProjectMember(user, id)) {
            return new ResponseEntity<>("You must be a project member to view this project!", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(project.get());
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignUser(@Valid @RequestBody ProjectMemberDto projectMember, @AuthenticationPrincipal MyUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (service.getProject(projectMember.projectId).isEmpty()) {
            return new ResponseEntity<>("Project doesn't exist!", HttpStatus.NOT_FOUND);
        }

        if (!service.isProjectMember(user, projectMember.projectId)) {
            return new ResponseEntity<>("You must be a project member!", HttpStatus.UNAUTHORIZED);
        }

        try {
            service.assignUser(projectMember);
        } catch (UnmetPreconditionException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("User assigned!");
    }

    @PostMapping("/unassign")
    public ResponseEntity<String> unassignUser(@Valid @RequestBody ProjectMemberDto projectMember, @AuthenticationPrincipal MyUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (service.getProject(projectMember.projectId).isEmpty()) {
            return new ResponseEntity<>("Project doesn't exist!", HttpStatus.NOT_FOUND);
        }

        if (!service.isProjectMember(user, projectMember.projectId)) {
            return new ResponseEntity<>("You must be a project member!", HttpStatus.UNAUTHORIZED);
        }

        try {
            service.unassignUser(projectMember);
        } catch (UnmetPreconditionException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("User unassigned!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable long id, @Valid @RequestBody ProjectDto project, @AuthenticationPrincipal MyUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (service.getProject(id).isEmpty()) {
            return new ResponseEntity<>("Project doesn't exist!", HttpStatus.NOT_FOUND);
        }

        if (!service.isProjectMember(user, id)) {
            return new ResponseEntity<>("You must be a project member!", HttpStatus.UNAUTHORIZED);
        }

        boolean updated = service.update(id, project);

        if (!updated) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/projects/" + id).toUriString());

        return ResponseEntity.created(uri).body("Project updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody ProjectDto project, @AuthenticationPrincipal MyUserDetails user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        Long id = service.create(project);

        try {
            var projectMember = new ProjectMemberDto();
            projectMember.username = user.getUsername();
            projectMember.projectId = id;

            service.assignUser(projectMember);
        } catch (UnmetPreconditionException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/projects/" + id).toUriString());

        return ResponseEntity.created(uri).body("Project created!");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @AuthenticationPrincipal MyUserDetails user) {
        if (service.getProject(id).isEmpty()) {
            return new ResponseEntity<>("Project doesn't exist!", HttpStatus.NOT_FOUND);
        }

        if (!service.isProjectMember(user, id)) {
            return new ResponseEntity<>("You must be a project member!", HttpStatus.UNAUTHORIZED);
        }

        service.delete(id);

        return ResponseEntity.ok("Project deleted!");
    }
}
