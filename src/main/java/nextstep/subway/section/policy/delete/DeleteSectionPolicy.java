package nextstep.subway.section.policy.delete;

import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;

import java.util.Objects;

public class DeleteSectionPolicy {
    public static void validate(Sections sections, Station station) {
        if (sections.size() == 1) {
            throw new RuntimeException("line's section is just one");
        }

        if (!Objects.equals(sections.getDownEndStation(), station)) {
            throw new RuntimeException("request's station is not line's downEndStation");
        }
    }
}
