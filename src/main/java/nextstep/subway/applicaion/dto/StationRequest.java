package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank(message = "지하철역 이름은 필수 입니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
