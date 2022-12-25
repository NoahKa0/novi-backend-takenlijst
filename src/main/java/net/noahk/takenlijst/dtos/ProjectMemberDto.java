package net.noahk.takenlijst.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ProjectMemberDto {

    @Min(1)
    public Long projectId;

    @NotBlank
    public String username;
}
