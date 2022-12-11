package net.noahk.takenlijst.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class CommentDto implements Comparable<CommentDto> {

    public long id;

    @NotBlank
    @Size(min=2, max=255)
    public String text;

    public long taskId;

    public LocalDateTime createdAt;

    @Override
    public int compareTo(CommentDto commentDto) {
        return createdAt.compareTo(commentDto.createdAt);
    }
}
