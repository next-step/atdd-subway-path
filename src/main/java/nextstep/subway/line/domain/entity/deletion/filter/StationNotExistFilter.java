package nextstep.subway.line.domain.entity.deletion.filter;

import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public class StationNotExistFilter implements SectionDeletionFilter{
    @Override
    public void doFilter(Sections sections, Station station) {
        if (!sections.hasStation(station)) {
            throw new DeletionValidationException(String.format("역이 존재하지 않습니다. 역 이름:%s", station.getId()));
        }
    }
}
