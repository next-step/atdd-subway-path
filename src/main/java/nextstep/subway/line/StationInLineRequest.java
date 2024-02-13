package nextstep.subway.line;

public class StationInLineRequest {
    private long stationId;
    private long nextStationId;
    private int distance;

    public long getStationId() {
        return stationId;
    }

    public long getNextStationId() {
        return nextStationId;
    }

    public int getDistance() {
        return distance;
    }
}
