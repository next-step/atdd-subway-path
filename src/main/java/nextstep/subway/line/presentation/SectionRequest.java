package nextstep.subway.line.presentation;


public class SectionRequest {

    private long downStationId;
    private long upStationId;
    private int distance;

    protected SectionRequest() {}

    public SectionRequest(long upStationId, long downStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
