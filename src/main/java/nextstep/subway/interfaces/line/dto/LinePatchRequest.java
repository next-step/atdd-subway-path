package nextstep.subway.interfaces.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LinePatchRequest {
    private String name;
    private String color;
}
