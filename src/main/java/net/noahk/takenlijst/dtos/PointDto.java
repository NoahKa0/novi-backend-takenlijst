package net.noahk.takenlijst.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class PointDto {

    public long id;

    @NotBlank
    @Size(min=2, max=255)
    public String description;

    @Min(1)
    public int expectedPoints;

    @Min(1)
    public int actualPoints;

    public long taskId;
}
