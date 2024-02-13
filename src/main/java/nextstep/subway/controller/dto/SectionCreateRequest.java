package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class SectionCreateRequest {
    @NotEmpty
    private String upStationId;
    @NotEmpty
    private String downStationId;
    @NotNull
    private Long distance;

    @Builder
    public SectionCreateRequest(String upStationId, String downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
