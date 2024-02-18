package nextstep.subway.line;


public class SectionRequest {

    private long downStationId;
    private long upStationId;
    private int distance;

    protected SectionRequest() {}

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
