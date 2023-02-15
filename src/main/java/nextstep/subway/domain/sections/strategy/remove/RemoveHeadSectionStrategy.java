package nextstep.subway.domain.sections.strategy.remove;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;

import java.util.Optional;

public class RemoveHeadSectionStrategy implements SectionsRemoveStrategy {

    @Override
    public void remove(Sections sections, Station station) {
        Optional<Section> backSection = sections.findSectionOnUpStation(station);

        Section headSection = backSection.get();
        sections.remove(headSection);
    }
}