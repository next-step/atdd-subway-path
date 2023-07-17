package nextstep.subway.service.request;

public class LineModifyRequest {

    private String name;
    private String color;

    public LineModifyRequest() {}

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
