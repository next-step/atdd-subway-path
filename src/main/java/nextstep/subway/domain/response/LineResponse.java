package nextstep.subway.domain.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Sections;
import nextstep.subway.domain.entity.Station;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
//    private Sections sections;
    private List<Station> stations;
    private int distance;
}
