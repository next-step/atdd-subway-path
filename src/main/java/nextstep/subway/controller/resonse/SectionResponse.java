package nextstep.subway.controller.resonse;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private long distance;

    private SectionResponse() {
    }

    private SectionResponse(long id, StationResponse upStation, StationResponse downStation, long distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section newSection) {
        StationResponse upStationResponse = new StationResponse(newSection.getUpStation());
        StationResponse downStationResponse = new StationResponse(newSection.getDownStation());
        return new SectionResponse(newSection.getId(), upStationResponse, downStationResponse, newSection.getDistance());
    }

    public long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public long getDistance() {
        return distance;
    }
}
