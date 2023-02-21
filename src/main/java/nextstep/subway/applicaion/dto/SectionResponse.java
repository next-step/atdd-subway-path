package nextstep.subway.applicaion.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class SectionResponse {

    private final Long id;
    private final StationResponse upStation;
    private final StationResponse downStation;
    private final Integer distance;

    public SectionResponse(Long id, StationResponse upStation, StationResponse downStation, Integer distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public Integer getDistance() {
        return distance;
    }

    @JsonIgnore
    public List<StationResponse> getStations() {
        return List.of(this.upStation, this.downStation);
    }

}
