package nextstep.subway.line.section;

public class SectionsUpdateRequest {
    private Long downStationId;
    private Long upStationId;
    private Long distance;

    public SectionsUpdateRequest(Long downStationId,
                                 Long upStationId,
                                 Long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
