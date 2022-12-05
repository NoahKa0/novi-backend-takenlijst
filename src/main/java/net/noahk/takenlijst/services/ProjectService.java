package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.ProjectDto;
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

            dto.id = item.getId();
            dto.name = item.getName();
            dto.projectLeaderId = item.getProjectLeaderId();

            list.add(dto);
        }
        return list;
    }

    public Optional<ProjectDto> getProject(Long id) {
        var item = repository.findById(id);
        if (item.isEmpty()) {
            return Optional.empty();
        }
        var dto = new ProjectDto();
        dto.id = item.get().getId();
        dto.name = item.get().getName();
        dto.projectLeaderId = item.get().getProjectLeaderId();
        return Optional.of(dto);
    }

    public boolean update(Long id, ProjectDto project) {
        var item = repository.findById(id);
        if (item.isPresent()) {
            var itemToUpdate = item.get();
            itemToUpdate.setName(project.name);
            repository.save(itemToUpdate);
            return true;
        }
        return false;
    }

    public Long createProject(ProjectDto project) {
        var toSave = new Project();

        toSave.setName(project.name);
        toSave.setProjectLeaderId(project.projectLeaderId);

        var result = repository.save(toSave);
        return result.getId();
    }
}
