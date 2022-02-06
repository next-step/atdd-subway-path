package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(Long downStationId, Long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public SectionRequest(Section section) {
        this.downStationId = section.getDownStation().getId();
        this.upStationId = section.getUpStation().getId();
        this.distance = section.getDistance();
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
