package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.AttachmentDto;
import net.noahk.takenlijst.security.MyUserDetails;
import net.noahk.takenlijst.services.AttachmentService;
import net.noahk.takenlijst.services.CommentService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

        // Add filename to headers for download.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(attachment.get().filename).build());

        // Set content type and set body to file bytes.
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(attachment.get().filetype))
                .headers(httpHeaders)
                .body(service.getAttachmentBytes(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> create(@PathVariable long id, @AuthenticationPrincipal MyUserDetails user, @RequestParam("file")MultipartFile file) {
        var commentDto = commentService.getComment(id);
        if (commentDto.isEmpty()) {
            return new ResponseEntity<>("First path variable must be a comment!", HttpStatus.BAD_REQUEST);
        }
        if (!commentDto.get().createdBy.equals(user.getUsername())) {
            return new ResponseEntity<>("You must be the owner of the comment!", HttpStatus.UNAUTHORIZED);
        }

        AttachmentDto dto = new AttachmentDto();
        dto.filename = file.getOriginalFilename();
        dto.filetype = file.getContentType(); // We need the file type to set the correct content type when the user downloads the file.

        // Read file bytes.
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong while reading file data!", HttpStatus.BAD_REQUEST);
        }

        Long retId = service.create(dto, id, bytes); // Bytes will be saved in the database.

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/attachments/" + retId).toUriString());

        return ResponseEntity.created(uri).body("Attachment created!");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @AuthenticationPrincipal MyUserDetails user) {
        var attachment = service.getAttachment(id);
        if (attachment.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if (!attachment.get().comment.createdBy.equals(user.getUsername())) {
            return new ResponseEntity<>("You must be the owner of the comment!", HttpStatus.UNAUTHORIZED);
        }

        service.delete(id);

        return ResponseEntity.ok("Attachment deleted!");
    }
}
