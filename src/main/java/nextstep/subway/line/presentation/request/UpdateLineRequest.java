package nextstep.subway.line.presentation.request;

public class UpdateLineRequest {

    private String color;

    private UpdateLineRequest() {
    }

    private UpdateLineRequest(String color) {
        this.color = color;
    }

    public static UpdateLineRequest of(String color) {
        return new UpdateLineRequest(color);
    }

    public String getColor() {
        return color;
    }

}
