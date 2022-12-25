package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.AttachmentDto;
import net.noahk.takenlijst.dtos.CommentDto;
import net.noahk.takenlijst.models.Attachment;
import net.noahk.takenlijst.models.Comment;
import net.noahk.takenlijst.repositories.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;

    public AttachmentService(AttachmentRepository repository) {this.repository = repository;}

    public Optional<AttachmentDto> getAttachment(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new AttachmentDto();

        dto = fillDto(item, dto);
        dto.comment = CommentService.fillDto(item.getComment(), new CommentDto());

        return Optional.of(dto);
    }

    public byte[] getAttachmentBytes(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return null;
        }
        var item = record.get();

        return item.getBytes();
    }

    public Long create(AttachmentDto attachment, long commentId, byte[] bytes) {
        var toSave = new Attachment();

        toSave = fillEntity(toSave, attachment);

        var comment = new Comment();
        comment.setId(commentId);
        toSave.setComment(comment);
        toSave.setBytes(bytes);

        var result = repository.save(toSave);
        return result.getId();
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    protected static Attachment fillEntity(Attachment entity, AttachmentDto dto) {
        entity.setFilename(dto.filename);
        entity.setFiletype(dto.filetype);

        return entity;
    }

    protected static AttachmentDto fillDto(Attachment entity, AttachmentDto dto) {
        dto.id = entity.getId();
        dto.filename = entity.getFilename();
        dto.filetype = entity.getFiletype();

        return dto;
    }
}
