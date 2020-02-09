package atdd.station.dto;

import javax.validation.constraints.NotBlank;

public class StationRequestDto {

    @NotBlank(message = "name 값은 필수 입니다.")
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
