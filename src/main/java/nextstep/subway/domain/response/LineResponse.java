package nextstep.subway.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Sections;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int distance;
    private Sections sections;
}
