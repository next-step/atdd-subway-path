package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.entity.Station;

public class SectionResponse {

    private final Long id;

    private final Station upStation;

    private final Station downStation;

    @JsonIgnore
    private int distance;

    public SectionResponse(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
