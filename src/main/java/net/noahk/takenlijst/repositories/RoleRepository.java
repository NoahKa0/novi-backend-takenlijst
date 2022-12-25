package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
}
