package nextstep.subway.line.update;

public class LineUpdateRequest {

    private String name;
    private String color;

    protected LineUpdateRequest() {
    }

    public LineUpdateRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
