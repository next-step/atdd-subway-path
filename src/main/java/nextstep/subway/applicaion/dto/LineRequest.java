package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Positive;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;

    @Positive(message = "distance must be a positive value")
    private int distance;

    public LineRequest() {}

    public LineRequest(String name, String color) {
        checkRequiredParams(name, color);
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        checkRequiredParams(name, color);
        checkStationParams(upStationId, downStationId);
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

    private void checkRequiredParams(String name, String color) {
        if (name == null) {
            throw new IllegalArgumentException("name must be a not null");
        } else if (color == null) {
            throw new IllegalArgumentException("color id must be a not null");
        }
    }

    private void checkStationParams(Long upStationId, Long downStationId) {
        if (upStationId == null) {
            throw new IllegalArgumentException("upStation id must be a not null");
        } else if (downStationId == null) {
            throw new IllegalArgumentException("downStation id must be a not null");
        }
    }
}