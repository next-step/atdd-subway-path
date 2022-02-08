package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    public SectionResponse(Section section) {
        this.id = section.getId();
        this.upStation = section.getUpStation();
        this.downStation = section.getDownStation();
        this.distance = section.getDistance();
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }
    public int getDistance() {
        return distance;
    }
}
