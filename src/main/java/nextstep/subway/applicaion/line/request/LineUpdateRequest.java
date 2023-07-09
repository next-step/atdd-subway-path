package nextstep.subway.applicaion.line.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineUpdateRequest {
    private String name;
    private String color;

    public LineUpdateRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }
}
