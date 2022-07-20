package nextstep.subway.applicaion.dto;

import lombok.ToString;

@ToString
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

    public Boolean requestValidCheck(){
        return this.upStationId != null && this.downStationId != null && this.getDistance() > 0;
    }
}
