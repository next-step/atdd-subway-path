package nextstep.subway.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    Long id;
    Line line;
    Station upStation;
    Station downStation;
    int distance;

}
