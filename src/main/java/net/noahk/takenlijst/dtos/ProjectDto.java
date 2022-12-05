package net.noahk.takenlijst.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProjectDto {

    public long id;

    @NotBlank
    @Size(min=2, max=100)
    public String name;

    public long projectLeaderId;

}
