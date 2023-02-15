package nextstep.subway.domain.strategy.remove;

import nextstep.subway.domain.SectionCollection;
import nextstep.subway.domain.Station;

import java.util.Objects;

public class BasicRemoveSection implements RemoveSectionStrategy{
    @Override
    public void removeSection(SectionCollection sectionCollection, Station station) {
        sectionCollection.getUpSection(station).ifPresent(sectionCollection::removeSection);
        sectionCollection.getDownSection(station).ifPresent(sectionCollection::removeSection);
    }
}
