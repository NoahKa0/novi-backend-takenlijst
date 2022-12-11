package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.CommentDto;
import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.models.Project;
import net.noahk.takenlijst.models.Task;
import net.noahk.takenlijst.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {this.repository = repository;}

    public Iterable<TaskDto> getTasks() {
        var items = repository.findAll();
        var list = new ArrayList<TaskDto>();

        for (var item : items) {
            var dto = new TaskDto();

            dto = fillDto(item, dto);

            list.add(dto);
        }
        return list;
    }

    public Optional<TaskDto> getTask(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new TaskDto();

        dto = fillDto(item, dto);

        dto.comments = new ArrayList<>();
        for(var comment : item.getComments()) {
            dto.comments.add(CommentService.fillDto(comment, new CommentDto()));
        }

        return Optional.of(dto);
    }

    public boolean update(Long id, TaskDto task) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();

            itemToUpdate = fillEntity(itemToUpdate, task);

            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long create(TaskDto task) {
        var toSave = new Task();

        toSave = fillEntity(toSave, task);

        var result = repository.save(toSave);
        return result.getId();
    }

    protected static Task fillEntity(Task entity, TaskDto dto) {
        entity.setName(dto.name);
        entity.setDescription(dto.description);
        if (dto.projectId != 0) {
            var project = new Project();
            project.setId(dto.projectId);
            entity.setProject(project);
        }

        return entity;
    }

    protected static TaskDto fillDto(Task entity, TaskDto dto) {
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();

        return dto;
    }
}
