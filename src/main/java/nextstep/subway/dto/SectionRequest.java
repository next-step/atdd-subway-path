package nextstep.subway.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Section;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest from(Section section) {
        return new SectionRequest(section.getUpStation().getId(),
                                  section.getDownStation().getId(),
                                  section.getDistance());
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
