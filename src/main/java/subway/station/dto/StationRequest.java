package subway.station.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class StationRequest {

    private final String NAME_NOT_BLANK_MESSAGE = "역 생성을 위해 역 이름은 필수 값 입니다.";
    @NotBlank(message = NAME_NOT_BLANK_MESSAGE)
    private String name;
}
