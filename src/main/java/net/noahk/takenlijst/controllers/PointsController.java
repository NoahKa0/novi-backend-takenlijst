package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.PointDto;
import net.noahk.takenlijst.services.PointService;
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
@RequestMapping("/points")
public class PointsController {

    private final PointService service;
    private final TaskService taskService;

    public PointsController(PointService service, TaskService taskService) {
        this.service = service;
        this.taskService = taskService;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<PointDto>> index() {
        return ResponseEntity.ok(service.getPoints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointDto> getPoints(@PathVariable long id) {
        var point = service.getPoint(id);
        if (point.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(point.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePoint(@PathVariable long id, @Valid @RequestBody PointDto point, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        boolean updated = service.update(id, point);

        if (!updated) {
            return new ResponseEntity<>("Not found!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/points/" + id).toUriString());

        return ResponseEntity.created(uri).body("Point updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody PointDto point, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        if (taskService.getTask(point.taskId).isEmpty()) {
            return new ResponseEntity<>("taskId: must exist!", HttpStatus.BAD_REQUEST);
        }

        Long id = service.create(point);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/points/" + id).toUriString());

        return ResponseEntity.created(uri).body("Point created!");
    }
}
