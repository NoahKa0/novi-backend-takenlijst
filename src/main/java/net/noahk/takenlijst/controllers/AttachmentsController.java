package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.AttachmentDto;
import net.noahk.takenlijst.services.AttachmentService;
import net.noahk.takenlijst.services.CommentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/attachments")
public class AttachmentsController {

    private final AttachmentService service;
    private final CommentService commentService;

    public AttachmentsController(AttachmentService service, CommentService commentService) {
        this.service = service;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable long id) {
        var attachment = service.getAttachment(id);
        if (attachment.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(attachment.get().filename).build());

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(attachment.get().filetype))
                .headers(httpHeaders)
                .body(service.getAttachmentBytes(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> create(@PathVariable long id, @RequestParam("file")MultipartFile file) {
        if (commentService.getComment(id).isEmpty()) {
            return new ResponseEntity<>("First path variable must be a comment!", HttpStatus.BAD_REQUEST);
        }

        AttachmentDto dto = new AttachmentDto();
        dto.filename = file.getOriginalFilename();
        dto.filetype = file.getContentType();

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong while reading file data!", HttpStatus.BAD_REQUEST);
        }

        Long retId = service.create(dto, id, bytes);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/attachments/" + retId).toUriString());

        return ResponseEntity.created(uri).body("Attachment created!");
    }
}
