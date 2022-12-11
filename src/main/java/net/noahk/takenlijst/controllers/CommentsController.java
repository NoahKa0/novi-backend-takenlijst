package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.CommentDto;
import net.noahk.takenlijst.services.CommentService;
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
@RequestMapping("/comments")
public class CommentsController {

    private final CommentService service;
    private final TaskService taskService;

    public CommentsController(CommentService service, TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<CommentDto>> index() {
        return ResponseEntity.ok(service.getComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable long id) {
        var comment = service.getComment(id);
        if (comment.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(comment.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable long id, @Valid @RequestBody CommentDto comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        boolean updated = service.update(id, comment);

        if (!updated) {
            return new ResponseEntity<>("Not found!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/comments/" + id).toUriString());

        return ResponseEntity.created(uri).body("Comment updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody CommentDto comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (taskService.getTask(comment.taskId).isEmpty()) {
            return new ResponseEntity<>("taskId: must exist!", HttpStatus.BAD_REQUEST);
        }

        Long id = service.create(comment);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/comments/" + id).toUriString());

        return ResponseEntity.created(uri).body("Comment created!");
    }
}
