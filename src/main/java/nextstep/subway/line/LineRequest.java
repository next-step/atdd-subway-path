package nextstep.subway.line;

public class LineRequest {

    private String name;
    private Color color;
    private long upStationId;
    private long downStationId;
    private int distance;


    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
