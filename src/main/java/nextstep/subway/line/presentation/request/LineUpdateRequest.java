package nextstep.subway.line.presentation.request;

import lombok.Getter;

@Getter
public class LineUpdateRequest {

    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
