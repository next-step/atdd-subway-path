package nextstep.subway.domain.section;

import nextstep.subway.domain.Station;

public class MiddleAddSection implements AddSectionStrategy {

    @Override
    public void addSection(SectionCollection sectionCollection, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        sectionCollection.getUpSection(upStation).ifPresent(updateSection -> updateSection.divideUpStation(downStation, distance));
        sectionCollection.getDownSection(downStation).ifPresent(updateSection -> updateSection.divideDownStation(upStation, distance));
        sectionCollection.addSection(section);
    }
}