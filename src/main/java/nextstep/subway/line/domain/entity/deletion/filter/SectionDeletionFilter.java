package nextstep.subway.line.domain.entity.deletion.filter;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public interface SectionDeletionFilter {

    void doFilter(Sections sections, Station station);
}
