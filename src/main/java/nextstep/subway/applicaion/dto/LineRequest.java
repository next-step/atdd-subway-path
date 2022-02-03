package nextstep.subway.applicaion.dto;

public class LineRequest {
    private static final int MIN_DISTANCE = 0;

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public boolean hasSectionInformation() {
        return upStationId != null && downStationId != null && distance > MIN_DISTANCE;
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
