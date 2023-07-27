package nextstep.subway.applicaion.dto;

public class LineUpdateRequest {

    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        if (name == null) {
            throw new IllegalArgumentException("name must be a not null");
        } else if (color == null) {
            throw new IllegalArgumentException("color id must be a not null");
        }
        this.name = name;
        this.color = color;
    }

    public LineUpdateRequest() {}

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}