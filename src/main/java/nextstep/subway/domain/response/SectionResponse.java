package nextstep.subway.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    Long id;
    Station upStation;
    Station downStation;
    int distance;
}
