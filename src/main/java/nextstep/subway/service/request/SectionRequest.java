package nextstep.subway.service.request;

public class SectionRequest {

    private long downStationId;
    private long upStationId;
    private int distance;

    public SectionRequest() {}

    public SectionRequest(long downStationId, long upStationId, int distance) {
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
