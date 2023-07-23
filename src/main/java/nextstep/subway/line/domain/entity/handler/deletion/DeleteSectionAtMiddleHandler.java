package nextstep.subway.line.domain.entity.handler.deletion;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public class DeleteSectionAtMiddleHandler extends SectionDeletionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Station station) {
        return !sections.getFirstStation().equals(station) && !sections.getLastStation().equals(station);
    }
}
