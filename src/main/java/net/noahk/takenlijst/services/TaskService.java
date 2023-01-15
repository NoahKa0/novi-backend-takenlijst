package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.CommentDto;
import net.noahk.takenlijst.dtos.LabelDto;
import net.noahk.takenlijst.dtos.PointDto;
import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.models.Label;
import net.noahk.takenlijst.models.Project;
import net.noahk.takenlijst.models.Task;
import net.noahk.takenlijst.models.User;
import net.noahk.takenlijst.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {this.repository = repository;}

    public ArrayList<TaskDto> getTasks() {
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

        if (item.getLabel() != null) {
            dto.label = LabelService.fillDto(item.getLabel(), new LabelDto());
        }

        dto.points = new ArrayList<>();
        for(var point : item.getPoints()) {
            dto.points.add(PointService.fillDto(point, new PointDto()));
        }

        dto.comments = new ArrayList<>();
        for(var comment : item.getComments()) {
            dto.comments.add(CommentService.fillDto(comment, new CommentDto()));
        }
        Collections.sort(dto.comments);

        return Optional.of(dto);
    }

    public List<TaskDto> getTasksByProject(Long id, boolean onlyNonCompleted) {
        var records = repository.getTasksByProjectId(id);
        var ret = new ArrayList<TaskDto>();

        for (var record : records) {
            if (onlyNonCompleted && record.getCompletedAt() != null) {
                continue;
            }
            var dto = new TaskDto();

            dto = fillDto(record, dto);

            if (record.getLabel() != null) {
                dto.label = LabelService.fillDto(record.getLabel(), new LabelDto());
            }

            ret.add(dto);
        }

        return ret;
    }

    public List<Integer> getBurnDown(long id, LocalDate start, LocalDate end, boolean predicted) {
        var records = repository.getTasksByProjectIdAndCompletedAtBetween(id, start, end);
        var ret = new ArrayList<Integer>();

        LocalDate current = start.minusDays(2);
        while (current.isBefore(end)) {
            current = current.plusDays(1);

            int total = 0;
            for (var record : records) {
                if (record.getCompletedAt().isAfter(current)) {
                    for (var point : record.getPoints()) {
                        if (predicted) {
                            total += point.getExpectedPoints();
                        } else {
                            total += point.getActualPoints();
                        }
                    }
                }
            }
            ret.add(total);
        }

        return ret;
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

    public void delete(long id) {
        repository.deleteById(id);
    }

    protected static Task fillEntity(Task entity, TaskDto dto) {
        entity.setName(dto.name);
        entity.setDescription(dto.description);
        entity.setCompletedAt(dto.completedAt);
        if (dto.projectId != 0) {
            var project = new Project();
            project.setId(dto.projectId);
            entity.setProject(project);
        }

        if (dto.labelId != 0) {
            var label = new Label();
            label.setId(dto.labelId);
            entity.setLabel(label);
        }

        if (dto.assignedUser != null && !dto.assignedUser.isEmpty()) {
            var user = new User();
            user.setUsername(dto.assignedUser);
            entity.setUser(user);
        } else {
            entity.setUser(null);
        }

        return entity;
    }

    protected static TaskDto fillDto(Task entity, TaskDto dto) {
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();
        dto.completedAt = entity.getCompletedAt();
        if (entity.getUser() != null) {
            dto.assignedUser = entity.getUser().getUsername();
        }
        if (entity.getProject() != null) {
            dto.projectId = entity.getProject().getId();
        }
        if (entity.getLabel() != null) {
            dto.labelId = entity.getLabel().getId();
        }

        return dto;
    }
}
