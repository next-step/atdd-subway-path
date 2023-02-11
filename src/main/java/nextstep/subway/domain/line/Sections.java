package nextstep.subway.domain.line;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(toList());
    }

    public void removeSection(Station station) {
        Section section = sections.get(sections.size() - 1);
        Station lastStation = section.getStations().get(1);
        if (!lastStation.equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }
}
