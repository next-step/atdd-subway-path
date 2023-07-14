package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineLastStations {
    @ManyToOne
    private Station upLastStation;

    @ManyToOne
    private Station downLastStation;

    protected LineLastStations() {}

    public LineLastStations(Station upLastStation, Station downLastStation) {
        if (upLastStation.equals(downLastStation)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        this.upLastStation = upLastStation;
        this.downLastStation = downLastStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upLastStation, downLastStation);
    }

    public Station getUpLastStation() {
        return upLastStation;
    }

    public Station getDownLastStation() {
        return downLastStation;
    }

    public void updateDownLastStation(Station station) {
        this.downLastStation = station;
    }

    public boolean isLastDownwardIsSameWithSectionUpwardStation(SectionStations sectionStations) {
        return downLastStation.equals(sectionStations.getUpStation());
    }

    public boolean isLastDownwardStation(Station station) {
        return downLastStation.equals(station);
    }
}
