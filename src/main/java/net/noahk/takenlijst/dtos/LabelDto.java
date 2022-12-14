package net.noahk.takenlijst.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class LabelDto {

    public long id;

    @NotBlank
    @Size(min=2, max=100)
    public String name;

    @Min(0)
    @Max(255)
    public int red;

    @Min(0)
    @Max(255)
    public int green;

    @Min(0)
    @Max(255)
    public int blue;

}
