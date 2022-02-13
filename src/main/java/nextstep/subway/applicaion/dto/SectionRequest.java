package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
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

    // TODO station 1D값 만 둬도되는지 질문
    public Section toSection() {
        return Section.builder()
                .upStation(new Station(upStationId))
                .downStation(new Station(downStationId))
                .distance(distance)
                .build();
    }
}
