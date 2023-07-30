package nextstep.subway.applicaion.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    public boolean hasSection() {
        return this.upStationId != null && this.downStationId != null && this.distance != 0;
    }
}
