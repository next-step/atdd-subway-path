package nextstep.subway.section.policy;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;

import java.util.Objects;
import java.util.Optional;

public class AddSectionPolicy {
    public static void validate(Sections sections, Section section) {
        Optional<Station> upStation = sections.getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getUpStation()))
                .findFirst();
        Optional<Station> downStation = sections.getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getDownStation()))
                .findFirst();
        Optional<Section> sectionEqualUpStation = sections.getSectionByUpStation(section.getUpStation());
        Optional<Section> sectionEqualDownStation = sections.getSectionByDownStation(section.getDownStation());

        checkRegisteredStation(upStation, downStation);
        checkNotRegisteredStation(upStation, downStation);
        checkSectionDistance(section, sectionEqualUpStation, sectionEqualDownStation);
    }

    private static void checkSectionDistance(Section section, Optional<Section> sectionEqualUpStation, Optional<Section> sectionEqualDownStation) {
        if (
                (sectionEqualUpStation.isPresent() && (sectionEqualUpStation.get().getDistance() <= section.getDistance()))
                ||(sectionEqualDownStation.isPresent() && (sectionEqualDownStation.get().getDistance() <= section.getDistance()))
        ) {
            throw new RuntimeException("Section's distance too long");
        }
    }

    private static void checkNotRegisteredStation(Optional<Station> upStation, Optional<Station> downStation) {
        if (upStation.isEmpty() && downStation.isEmpty()) {
            throw new RuntimeException("Section's stations not exist in sections");
        }
    }

    private static void checkRegisteredStation(Optional<Station> upStation, Optional<Station> downStation) {
        if (upStation.isPresent() && downStation.isPresent()) {
            throw new RuntimeException("Section's stations already registered");
        }
    }
}
