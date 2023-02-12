package nextstep.subway.applicaion.dto;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(LineRequest lineRequest) {
        this(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        notNull(upStationId, "upStationId must not be null");
        notNull(downStationId, "upStationId must not be null");
        isTrue(distance > 0, "distance must not be greater than zero");
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
}
