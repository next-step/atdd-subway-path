package nextstep.subway.domain.sections.strategy.remove;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;

public class SectionsRemover {

    private SectionsRemoveStrategy strategy;
    private Sections sections;

    public SectionsRemover(Sections sections) {
        this.sections = sections;
    }

    public void setStrategy(SectionsRemoveStrategy strategy) {
        this.strategy = strategy;
    }

    public void remove(Station station) {
        strategy.remove(sections, station);
    }
}
