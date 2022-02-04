package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public SectionRequest(Section section) {
        this.downStationId = section.getDownStation().getId();
        this.upStationId = section.getUpStation().getId();
        this.distance = section.getDistance();
    }
}
