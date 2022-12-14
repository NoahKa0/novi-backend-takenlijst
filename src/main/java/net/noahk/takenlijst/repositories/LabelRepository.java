package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Label;
import org.springframework.data.repository.CrudRepository;

public interface LabelRepository extends CrudRepository<Label, Long> {
}
