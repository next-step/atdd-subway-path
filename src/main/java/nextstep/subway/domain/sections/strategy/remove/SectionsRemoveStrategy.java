package nextstep.subway.domain.sections.strategy.remove;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;

public interface SectionsRemoveStrategy {
    void remove(Sections sections, Station station);
}
