package nextstep.subway.line.domain.entity.handler.deletion;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public abstract class SectionDeletionHandler {
    public boolean checkApplicable(Sections sections, Station station) {
        return false;
    }
}
