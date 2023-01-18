package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.ProjectDto;
import net.noahk.takenlijst.dtos.ProjectMemberDto;
import net.noahk.takenlijst.dtos.TaskDto;
import net.noahk.takenlijst.exceptions.UnmetPreconditionException;
import net.noahk.takenlijst.models.Project;
import net.noahk.takenlijst.models.User;
import net.noahk.takenlijst.repositories.ProjectRepository;
import net.noahk.takenlijst.repositories.UserRepository;
import net.noahk.takenlijst.security.MyUserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

        var result = repository.save(toSave);
        return result.getId();
    }

    public void assignUser(ProjectMemberDto projectMember) throws UnmetPreconditionException {
        var project = repository.findById(projectMember.projectId);
        var user = userRepository.findById(projectMember.username);

        if (project.isEmpty()) {
            throw new UnmetPreconditionException("Project not found!");
        }
        if (user.isEmpty()) {
            throw new UnmetPreconditionException("User not found!");
        }

        var toSave = project.get();
        var members = toSave.getMembers();
        if (members == null) {
            members = new ArrayList<User>();
        }
        for (var member : members) {
            if (member.getUsername().equals(projectMember.username)) {
                throw new UnmetPreconditionException("User already assigned!");
            }
        }
        members.add(user.get());
        toSave.setMembers(members);
        repository.save(toSave);
    }

    public void unassignUser(ProjectMemberDto projectMember) throws UnmetPreconditionException {
        var project = repository.findById(projectMember.projectId);

        if (project.isEmpty()) {
            throw new UnmetPreconditionException("Project not found!");
        }

        var toSave = project.get();
        var members = toSave.getMembers();
        User user = null;
        var projectLeaderCount = 0;
        for (var member : members) {
            if (member.getUsername().equals(projectMember.username)) {
                user = member;
            }
            var roles = member.getRoles();
            var isLeader = roles.stream().anyMatch(role -> role.getRolename().equals("TEAM_LEADER"));
            if (isLeader) {
                projectLeaderCount++;
            }
        }
        if (user == null) {
            throw new UnmetPreconditionException("User not in project!");
        }
        var roles = user.getRoles();
        var isLeader = roles.stream().anyMatch(role -> role.getRolename().equals("TEAM_LEADER"));
        if (projectLeaderCount == 1 && isLeader) {
            throw new UnmetPreconditionException("Cannot unassign user since there would be no leaders left!");
        }
        members.removeIf((x) -> x.getUsername().equals(projectMember.username));
        toSave.setMembers(members);
        repository.save(toSave);
    }

    public boolean isProjectMember(MyUserDetails user, long projectId) {
        if (user == null) {
            return false;
        }
        var project = repository.findById(projectId);
        if (project.isEmpty()) {
            return false;
        }

        var members = project.get().getMembers();
        for (var member : members) {
            if (member.getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    protected static Project fillEntity(Project entity, ProjectDto dto) {
        entity.setName(dto.name);

        return entity;
    }

    protected static ProjectDto fillDto(Project entity, ProjectDto dto) {
        dto.id = entity.getId();
        dto.name = entity.getName();

        dto.members = new ArrayList<String>();
        if (entity.getMembers() != null) {
            for (var member : entity.getMembers()) {
                if (!dto.members.contains(member.getUsername())) {
                    dto.members.add(member.getUsername());
                }
            }
        }

        return dto;
    }
}
