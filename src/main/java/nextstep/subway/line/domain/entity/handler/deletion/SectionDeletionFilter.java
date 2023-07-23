package nextstep.subway.line.domain.entity.handler.deletion;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

interface SectionDeletionFilter {

    void doFilter(Sections sections, Station station);
}
