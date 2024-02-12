package nextstep.subway.dto.line;

public class LineUpdateRequest {
    private String name;
    private String color;

    protected LineUpdateRequest() {
    }

    public LineUpdateRequest(
        String name,
        String color
    ) {
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
