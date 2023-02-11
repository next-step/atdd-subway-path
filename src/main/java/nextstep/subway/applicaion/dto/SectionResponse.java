package nextstep.subway.applicaion.dto;

public class SectionResponse {

    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Integer distance;

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
}
