package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Task;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

    public List<Task> getTasksByProjectId(long projectId);

    public List<Task> getTasksByCompletedAtBetween(LocalDate start, LocalDate end);
}
