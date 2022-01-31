package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private Long downStationId;
    private Long upStationId;
    private int distance;

    public static SectionResponse of(final Section section) {
        return new SectionResponse(section.getDownStation().getId(), section.getUpStation().getId(), section.getDistance());
    }

    private SectionResponse(final Long downStationId, final Long upStationId, final int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
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
}
