package nextstep.subway.line;

public class UpdateLineRequest {

    private final String name;
    private final String color;

    public UpdateLineRequest(String name, String color) {
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
