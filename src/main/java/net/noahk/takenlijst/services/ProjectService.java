package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.ProjectDto;
import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.models.Project;
import net.noahk.takenlijst.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public Iterable<ProjectDto> getProjects() {
        var items = repository.findAll();
        var list = new ArrayList<ProjectDto>();

        for (var item : items) {
            var dto = new ProjectDto();

            dto = fillDto(item, dto);

            list.add(dto);
        }
        return list;
    }

    public Optional<ProjectDto> getProject(Long id) {
        var record = repository.findById(id);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var item = record.get();

        var dto = new ProjectDto();
        dto = fillDto(item, dto);

        dto.tasks = new ArrayList<>();
        for(var task : item.getTasks()) {
            dto.tasks.add(TaskService.fillDto(task, new TaskDto()));
        }

        return Optional.of(dto);
    }

    public boolean update(Long id, ProjectDto project) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();

            itemToUpdate = fillEntity(itemToUpdate, project);

            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long create(ProjectDto project) {
        var toSave = new Project();

        toSave = fillEntity(toSave, project);
        toSave.setProjectLeaderId(project.projectLeaderId);

        var result = repository.save(toSave);
        return result.getId();
    }

    protected static Project fillEntity(Project entity, ProjectDto dto) {
        entity.setName(dto.name);

        return entity;
    }

    protected static ProjectDto fillDto(Project entity, ProjectDto dto) {
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.projectLeaderId = entity.getProjectLeaderId();

        return dto;
    }
}
