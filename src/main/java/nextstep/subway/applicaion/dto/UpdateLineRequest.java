package nextstep.subway.applicaion.dto;

public class UpdateLineRequest {

    private String name;
    private String color;

    private UpdateLineRequest() {
    }

    public UpdateLineRequest(final String name, final String color) {
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
