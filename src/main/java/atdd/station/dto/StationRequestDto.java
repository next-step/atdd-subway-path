package atdd.station.dto;

import javax.validation.constraints.NotBlank;

public class StationRequestDto {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StationRequestDto{" +
                "name='" + name + '\'' +
                '}';
    }

}
