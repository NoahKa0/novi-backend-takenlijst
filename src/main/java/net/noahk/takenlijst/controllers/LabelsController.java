package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.LabelDto;
import net.noahk.takenlijst.services.LabelService;
import net.noahk.takenlijst.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/labels")
public class LabelsController {

    private final LabelService service;

    public LabelsController(LabelService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<LabelDto>> index() {
        return ResponseEntity.ok(service.getLabels());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLabel(@PathVariable long id, @Valid @RequestBody LabelDto label, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        boolean updated = service.update(id, label);

        if (!updated) {
            return new ResponseEntity<>("Not found!", HttpStatus.BAD_REQUEST);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/labels").toUriString());

        return ResponseEntity.created(uri).body("Label updated!");
    }

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody LabelDto label, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Util.getBindingResultResponse(bindingResult);
        }

        Long id = service.create(label);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/labels").toUriString());

        return ResponseEntity.created(uri).body("Label created!");
    }
}
