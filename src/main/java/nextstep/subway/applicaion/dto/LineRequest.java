package nextstep.subway.applicaion.dto;


import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long downStationId;
    private Long upStationId;
    private int distance;

    protected LineRequest() {
    }


    public LineRequest(String name, String color, Long downStationId, Long upStationId, int distance) {
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