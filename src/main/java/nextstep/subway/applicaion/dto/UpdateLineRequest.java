package nextstep.subway.applicaion.dto;

public class UpdateLineRequest {

    private String color;
    private String name;

    public static UpdateLineRequest of(String color, String name) {
        UpdateLineRequest request = new UpdateLineRequest();
        request.color = color;
        request.name = name;

        return request;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

}
