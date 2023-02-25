package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class UpmostSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public boolean isValidCondition(Sections sections, Long stationId) {
        List<Section> sectionsList = sections.getValue();
        if (sectionsList.isEmpty()) {
            return false;
        }

        return sectionsList.get(0).isSameUpStation(stationId);
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Long stationId, Line line) {
        List<Section> sectionsList = sections.getValue();

        Section deprecatedSection = sectionsList.get(0);

        return new ChangeableSections(List.of(), List.of(deprecatedSection));
    }
}
