package nextstep.subway.domain.section;

import nextstep.subway.domain.Station;

public class BasicRemoveSection implements RemoveSectionStrategy{
    @Override
    public void removeSection(SectionCollection sectionCollection, Station station) {
        sectionCollection.getUpSection(station).ifPresent(sectionCollection::removeSection);
        sectionCollection.getDownSection(station).ifPresent(sectionCollection::removeSection);
    }
}
