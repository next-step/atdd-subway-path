package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.station.domain.Station;

@Embeddable
public class SectionStations {

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    protected SectionStations() {}

    public SectionStations(Station upStation, Station downStation) {
        if (upStation == null || downStation == null || upStation.equals(downStation)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean checkStationInSection(Station st) {
        return upStation.equals(st) || downStation.equals(st);
    }
}
