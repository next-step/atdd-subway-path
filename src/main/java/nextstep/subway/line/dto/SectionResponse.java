package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Section;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionResponse(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getLine().getId(),
                section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance());
    }
}
