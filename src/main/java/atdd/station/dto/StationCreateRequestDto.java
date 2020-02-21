package atdd.station.dto;

import javax.validation.constraints.NotBlank;

public class StationCreateRequestDto {

    @NotBlank(message = "name 값은 필수 입니다.")
    private String name;

    public static StationCreateRequestDto of(String name) {
        StationCreateRequestDto requestDto = new StationCreateRequestDto();
        requestDto.name = name;
        return requestDto;
    }

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
