package nextstep.subway.line.addsection;

public class LineAddSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    protected LineAddSectionRequest() {
    }

    public LineAddSectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
