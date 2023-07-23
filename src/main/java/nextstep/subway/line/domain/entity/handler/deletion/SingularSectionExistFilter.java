package nextstep.subway.line.domain.entity.handler.deletion;

import nextstep.subway.common.exception.DeletionValidationException;
import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public class SingularSectionExistFilter implements SectionDeletionFilter{
    @Override
    public void doFilter(Sections sections, Station station) {
        if (sections.size() == 1) {
            throw new DeletionValidationException("section.is.singular");
        }
    }
}
