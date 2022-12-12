package net.noahk.takenlijst.dtos;

import net.noahk.takenlijst.models.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class TaskDto {

    public long id;

    @NotBlank
    @Size(min=2, max=100)
    public String name;

    @NotBlank
    @Size(min=2, max=999)
    public String description;

    public long projectId;

    public List<CommentDto> comments;

}
