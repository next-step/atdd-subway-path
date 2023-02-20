package nextstep.subway.domain.section;

import nextstep.subway.domain.Station;

import java.util.Objects;

public class AddSectionFactory {
    public AddSectionStrategy createAddSection(SectionCollection sectionCollection, Section section) {
        if (sectionCollection.isEmpty()) {
            return new BasicAddSection();
        }
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Station firstStation = sectionCollection.getFirstStation();
        Station lastStation = sectionCollection.getLastStation();

        if (Objects.equals(downStation, firstStation) || Objects.equals(upStation, lastStation)) {
            return new BasicAddSection();
        }

        return new MiddleAddSection();
    }
}
