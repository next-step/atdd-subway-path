package nextstep.subway.line.service.dto;

import nextstep.subway.line.repository.domain.Section;
import nextstep.subway.station.service.dto.StationResponse;

public class SectionResponse {
    private Long id;

    private StationResponse upStation;

    private StationResponse downStation;

    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(final Long id, final StationResponse upStation, final StationResponse downStation, final int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse from(final Section savedSection) {
        return new SectionResponse(
                savedSection.getId(),
                StationResponse.from(savedSection.getUpStation()),
                StationResponse.from(savedSection.getDownStation()),
                savedSection.getDistance()
        );
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
