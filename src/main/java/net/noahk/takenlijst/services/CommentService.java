package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.AttachmentDto;
import net.noahk.takenlijst.dtos.CommentDto;
import net.noahk.takenlijst.models.Comment;
import net.noahk.takenlijst.models.Task;
import net.noahk.takenlijst.models.User;
import net.noahk.takenlijst.repositories.CommentRepository;
import net.noahk.takenlijst.security.MyUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {this.repository = repository;}

    public Iterable<CommentDto> getComments() {
        var items = repository.findAll();
        var list = new ArrayList<CommentDto>();

        for (var item : items) {
            var dto = new CommentDto();

            dto = fillDto(item, dto);

            list.add(dto);
        }
        return list;
    }

    public Optional<CommentDto> getComment(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new CommentDto();

        dto = fillDto(item, dto);

        return Optional.of(dto);
    }

    public boolean update(Long id, CommentDto comment) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();

            itemToUpdate = fillEntity(itemToUpdate, comment);

            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long create(CommentDto comment, MyUserDetails user) {
        var toSave = new Comment();

        toSave = fillEntity(toSave, comment);
        toSave.setCreatedAt(LocalDateTime.now());

        var userModel = new User();
        userModel.setUsername(user.getUsername());
        toSave.setUser(userModel);

        var result = repository.save(toSave);
        return result.getId();
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    protected static Comment fillEntity(Comment entity, CommentDto dto) {
        entity.setText(dto.text);
        if (dto.taskId != 0) {
            var task = new Task();
            task.setId(dto.taskId);
            entity.setTask(task);
        }

        return entity;
    }

    protected static CommentDto fillDto(Comment entity, CommentDto dto) {
        dto.id = entity.getId();
        dto.text = entity.getText();
        dto.createdBy = entity.getUser().getUsername();
        dto.createdAt = entity.getCreatedAt();

        var attachments = entity.getAttachments();
        dto.attachments = new ArrayList<AttachmentDto>();
        for (var attachment : attachments) {
            dto.attachments.add(AttachmentService.fillDto(attachment, new AttachmentDto()));
        }
        if (entity.getTask() != null) {
            dto.taskId = entity.getTask().getId();
        }

        return dto;
    }
}
