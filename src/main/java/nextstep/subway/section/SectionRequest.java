package nextstep.subway.section;

public class SectionRequest {
    private long upStationId;
    private long downStationId;
    private long distance;
    private int index;

    public SectionRequest(long upStationId, long downStationId, long distance, int index) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.index = index;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
