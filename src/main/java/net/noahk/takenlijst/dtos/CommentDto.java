package net.noahk.takenlijst.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class CommentDto implements Comparable<CommentDto> {

    public long id;

    @NotBlank
    @Size(min=2, max=255)
    public String text;

    public long taskId;

    @Null
    public String createdBy;

    @Null
    public LocalDateTime createdAt;

    public List<AttachmentDto> attachments;

    @Override
    public int compareTo(CommentDto commentDto) {
        return createdAt.compareTo(commentDto.createdAt);
    }
}
