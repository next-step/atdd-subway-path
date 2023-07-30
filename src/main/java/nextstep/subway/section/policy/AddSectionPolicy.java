package nextstep.subway.section.policy;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;

import java.util.Objects;
import java.util.Optional;

public class AddSectionPolicy {
    public static void validate(Sections sections, Section section) {
        Optional<Section> sectionEqualUpStation = sections.getSectionByUpStation(section.getUpStation());
        Optional<Section> sectionEqualDownStation = sections.getSectionByDownStation(section.getDownStation());

        if (
                (sectionEqualUpStation.isPresent() && (sectionEqualUpStation.get().getDistance() <= section.getDistance()))
                ||(sectionEqualDownStation.isPresent() && (sectionEqualDownStation.get().getDistance() <= section.getDistance()))
        ) {
            throw new RuntimeException("Section's distance too long");
        }
    }
}
