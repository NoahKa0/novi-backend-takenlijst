package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Point;
import org.springframework.data.repository.CrudRepository;

public interface PointRepository extends CrudRepository<Point, Long> {
}
