package nextstep.subway.section.policy;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;

import java.util.Objects;

public class AddSectionPolicy {
    public static void validate(Sections sections, Section section) {
        if (!Objects.equals(sections.getDownEndStation(), section.getUpStation())) {
            throw new RuntimeException("section's upStation is not line's downEndStation");
        }

        if (sections.getAllStation().contains(section.getDownStation())) {
            throw new RuntimeException("section's downStation is already included in line");
        }
    }
}
