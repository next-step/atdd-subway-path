package nextstep.subway.line.presentation.request;

public class AddSectionRequest {

    private Long upStationId;

    private Long downStationId;

    private Integer distance;

    private AddSectionRequest() {
    }

    private AddSectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static AddSectionRequest of(Long upStationId, Long downStationId, Integer distance) {
        return new AddSectionRequest(upStationId, downStationId, distance);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

}
