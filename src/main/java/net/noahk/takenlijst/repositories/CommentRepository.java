package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
