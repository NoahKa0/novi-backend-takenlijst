package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
