package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

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

    public boolean isSaveLineRequestValid() {
        if (upStationId == null) {
            return false;
        }

        if (downStationId == null) {
            return false;
        }

        return distance != 0;
    }

    public Line toLine() {
        return Line.builder()
                .name(name)
                .color(color)
                .build();
    }
}
