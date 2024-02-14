package nextstep.subway.line;

public class LineRequest {
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line createLine() {
        return new Line(name, color);
    }
}
