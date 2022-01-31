package nextstep.subway.applicaion.dto;


import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;       // 상행 종점
    private Long downStationId;     // 하행 종점
    private int distance;           // 거리

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

    public Line toEntity() {
        return new Line(name, color);
    }
}
