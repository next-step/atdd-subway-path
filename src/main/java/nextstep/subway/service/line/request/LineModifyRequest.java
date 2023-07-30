package nextstep.subway.service.line.request;

public class LineModifyRequest {

    private String name;
    private String color;

    public LineModifyRequest(String name, String color) {
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
