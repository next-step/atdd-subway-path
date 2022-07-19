package nextstep.subway.applicaion.dto;

public class LineUpdateRequest {

    private String name;
    private String color;

    public LineUpdateRequest() {
    }

    public LineUpdateRequest(final String name, final String color) {
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
