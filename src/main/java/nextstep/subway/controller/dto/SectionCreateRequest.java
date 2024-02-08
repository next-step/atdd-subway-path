package nextstep.subway.controller.dto;

import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
public class SectionCreateRequest {

    @NotNull
    private Long downStationId;
    @NotNull
    private Long upStationId;
    @Min(1)
    private long distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(Long downStationId, Long upStationId, long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public List<Long> stationIds(){
        return List.of(upStationId, downStationId);
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public long getDistance() {
        return distance;
    }
}
