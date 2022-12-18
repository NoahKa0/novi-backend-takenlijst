package net.noahk.takenlijst.repositories;

import net.noahk.takenlijst.models.Attachment;
import org.springframework.data.repository.CrudRepository;

public interface AttachmentRepository extends CrudRepository<Attachment, Long> {
}
