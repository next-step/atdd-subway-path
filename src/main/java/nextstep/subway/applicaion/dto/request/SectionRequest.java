package nextstep.subway.applicaion.dto.request;

import lombok.Getter;

@Getter
public class SectionRequest {

    private Long upStationId;

    private Long downStationId;

    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
