package nextstep.subway.line.presentation.request;

public class UpdateLineRequest {

    private String color;

    private Integer distance;

    private UpdateLineRequest() {
    }

    private UpdateLineRequest(String color, Integer distance) {
        this.color = color;
        this.distance = distance;
    }

    public static UpdateLineRequest of(String color, Integer distance) {
        return new UpdateLineRequest(color, distance);
    }

    public String getColor() {
        return color;
    }

    public Integer getDistance() {
        return distance;
    }

}
