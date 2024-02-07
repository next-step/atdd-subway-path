package nextstep.subway.controller.dto;

import nextstep.subway.service.dto.AddSectionCommand;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class SectionCreateRequestBody {
    @NotBlank
    private Long upStationId;
    @NotBlank
    private Long downStationId;
    @NotBlank
    @Min(value = 1)
    private int distance;

    public SectionCreateRequestBody(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public AddSectionCommand toCommand(Long lineId) {
        return new AddSectionCommand(lineId, upStationId, downStationId, distance);
    }
}
