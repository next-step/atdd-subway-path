package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.upStation = new StationResponse(section.getUpStation());
        this.downStation = new StationResponse(section.getDownStation());
        this.distance = section.getDistance();
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
