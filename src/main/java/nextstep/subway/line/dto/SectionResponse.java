package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;
    private int duration;

    public SectionResponse(StationResponse upStation, StationResponse downStation, int distance, int duration) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.duration = duration;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(StationResponse.of(section.getUpStation()), StationResponse.of(section.getDownStation()), section.getDistance(), section.getDuration());
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

    public int getDuration() {
        return duration;
    }
}
