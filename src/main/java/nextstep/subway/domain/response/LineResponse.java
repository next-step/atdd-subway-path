package nextstep.subway.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;
    private int distance;
    private List<Section> sections;
}
