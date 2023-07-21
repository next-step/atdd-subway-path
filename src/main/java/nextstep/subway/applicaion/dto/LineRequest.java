package nextstep.subway.applicaion.dto;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {}

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        if(upStationId == null) {
            throw new IllegalArgumentException("upStation id must be a not null");
        } else if (upStationId == null) {
            throw new IllegalArgumentException("downStation id must be a not null");
        }

        if (distance < 1) {
            throw new IllegalArgumentException("Distance must be a positive value");
        }

        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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