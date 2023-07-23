package nextstep.subway.line.domain.entity.deletion.handler;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public abstract class SectionDeletionHandler {
    public abstract boolean checkApplicable(Sections sections, Station station);

    public abstract void apply(Sections sections, Station station);
}
