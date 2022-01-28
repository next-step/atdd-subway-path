package nextstep.subway.station.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationsDto {
    private Station upStation;
    private Station downStation;

    public StationsDto(Station upStation, Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }
}
