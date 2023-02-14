package nextstep.subway.domain.strategy.remove;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;
import nextstep.subway.domain.Station;

import java.util.Optional;

public class MiddleRemoveSection implements RemoveSectionStrategy{
    @Override
    public void removeSection(SectionCollection sectionCollection, Station station) {
        Section downSection = sectionCollection.getDownSection(station).orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
        Section upSection = sectionCollection.getUpSection(station).orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));

        int distance = downSection.getDistance() + upSection.getDistance();
        Section connectSection = new Section(downSection.getLine(), downSection.getUpStation(), upSection.getDownStation(), distance);

        sectionCollection.removeSection(downSection);
        sectionCollection.removeSection(upSection);
        sectionCollection.addSection(connectSection);
    }
}
