package nextstep.subway.line.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineModifyRequest {
    private String name;
    private String color;
}
