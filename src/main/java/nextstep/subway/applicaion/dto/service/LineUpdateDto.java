package nextstep.subway.applicaion.dto.service;

public class LineUpdateDto {

    private final String name;

    private final String color;

    public LineUpdateDto(String name, String color) {
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
