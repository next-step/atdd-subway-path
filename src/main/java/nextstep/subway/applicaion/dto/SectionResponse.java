package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    private Station upStation;
    private Station downStation;
    private int distance;

}
