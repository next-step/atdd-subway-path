package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class DownmostSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public boolean meetCondition(Sections sections, Long stationId) {
        List<Section> sectionsList = sections.getValue();
        if (sectionsList.isEmpty()) {
            return false;
        }

        return sectionsList.get(sectionsList.size() - 1).isSameDownStation(stationId);
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Long stationId, Line line) {
        List<Section> sectionsList = sections.getValue();

        Section deprecatedSection = sectionsList.get(sectionsList.size() - 1);

        return new ChangeableSections(List.of(), List.of(deprecatedSection));
    }
}
