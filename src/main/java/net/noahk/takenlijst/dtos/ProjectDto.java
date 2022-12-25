package net.noahk.takenlijst.dtos;

import net.noahk.takenlijst.models.Task;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class ProjectDto {

    public long id;

    @NotBlank
    @Size(min=2, max=100)
    public String name;

    public List<TaskDto> tasks;

    public List<String> members;

}
