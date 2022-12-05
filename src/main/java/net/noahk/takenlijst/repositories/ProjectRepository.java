package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Project;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Long> {
}
