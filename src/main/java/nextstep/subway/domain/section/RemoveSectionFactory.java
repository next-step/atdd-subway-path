package nextstep.subway.domain.section;

import nextstep.subway.domain.Station;

import java.util.Objects;

public class RemoveSectionFactory {
    public RemoveSectionStrategy createRemoveSectionStrategy(SectionCollection sectionCollection, Station station) {

        Station firstStation = sectionCollection.getFirstStation();
        Station lastStation = sectionCollection.getLastStation();

        if (Objects.equals(station, firstStation) || Objects.equals(station, lastStation)) {
            return new BasicRemoveSection();
        }

        return new MiddleRemoveSection();
    }
}
