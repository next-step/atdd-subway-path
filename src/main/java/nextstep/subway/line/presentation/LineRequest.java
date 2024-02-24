package nextstep.subway.line.presentation;

import nextstep.subway.line.domain.Color;

public class LineRequest {

    private String name;
    private Color color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public LineRequest(String name, Color color, long upStationId, long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

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
