package nextstep.subway.domain.sections.strategy.remove;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;

import java.util.Optional;

public class RemoveInternalSectionStrategy implements SectionsRemoveStrategy {

    @Override
    public void remove(Sections sections, Station station) {
        Optional<Section> frontSection = sections.findSectionOnDownStation(station);
        Optional<Section> backSection = sections.findSectionOnUpStation(station);

        sections.remove(frontSection.get());
        sections.remove(backSection.get());

        Section mergeSection = sections.mergeSection(frontSection.get(), backSection.get());
        sections.add(mergeSection);
    }
}