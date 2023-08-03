package nextstep.subway.section.policy.add;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;

import java.util.Objects;
import java.util.Optional;

public class CheckNotRegisterPolicy implements AddSectionPolicy {
    @Override
    public void validate(Sections sections, Section section) {
        Optional<Station> upStation = sections.getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getUpStation()))
                .findFirst();
        Optional<Station> downStation = sections.getAllStation().stream()
                .filter(station -> Objects.equals(station, section.getDownStation()))
                .findFirst();

        if (upStation.isEmpty() && downStation.isEmpty()) {
            throw new RuntimeException("Section's stations not exist in sections");
        }
    }
}
