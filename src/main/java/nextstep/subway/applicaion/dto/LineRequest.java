package nextstep.subway.applicaion.dto;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId,
        int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineRequest of(String name, String color, Long upStationId, Long downStationId,
                    int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public static LineRequest of(String name, String color) {
        return new LineRequest(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

}
