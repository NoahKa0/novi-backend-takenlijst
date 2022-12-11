package net.noahk.takenlijst.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentDto {

    public long id;

    @NotBlank
    @Size(min=2, max=255)
    public String text;

    public long taskId;

}
