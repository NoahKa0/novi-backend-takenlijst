package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
