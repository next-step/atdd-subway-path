package nextstep.subway.line.service.dto;

import nextstep.subway.line.exception.CreateRequestNotValidException;

import java.util.Objects;

public class SectionCreateRequest {
    private Long downStationId;
    private Long upStationId;
    private int distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void validate() {
        if (Objects.isNull(upStationId)) {
            throw new CreateRequestNotValidException("upStationId can not be null");
        }
        if (Objects.isNull(downStationId)) {
            throw new CreateRequestNotValidException("downStationId can not be null");
        }
        if (downStationId.equals(upStationId)) {
            throw new CreateRequestNotValidException("upStationId and downStationId can not be the same");
        }
        if (distance <= 0) {
            throw new CreateRequestNotValidException("distance must be greater than 0");
        }
    }
}
