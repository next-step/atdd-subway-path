package subway.section;

import subway.line.Line;
import subway.station.Station;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
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

    public Long getDistance() {
        return distance;
    }

    public Section toEntity(Station upStation, Station downStation, Line line) {
        return new Section(upStation, downStation, distance, line);
    }
}
