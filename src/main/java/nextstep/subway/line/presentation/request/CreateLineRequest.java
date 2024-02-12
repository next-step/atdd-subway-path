package nextstep.subway.line.presentation.request;

public class CreateLineRequest {

    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private Integer distance;

    private CreateLineRequest() {
    }

    private CreateLineRequest(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static CreateLineRequest of(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        return new CreateLineRequest(name, color, upStationId, downStationId, distance);
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

    public Integer getDistance() {
        return distance;
    }

}
