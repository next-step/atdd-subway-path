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
import java.util.Optional;

@Getter
@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        Section section = sections.get(0);
        List<Station> stations = new ArrayList<>();
        stations.addAll(getUpperStations(section.getUpStation()));
        stations.addAll(getDownerStations(section.getDownStation()));

        return Collections.unmodifiableList(stations);
    }

    private List<Station> getUpperStations(Station upStation) {
        List<Station> list = new ArrayList<>();
        list.add(upStation);
        Optional<Section> maybeUpSection = sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .findFirst();
        while (maybeUpSection.isPresent()) {
            Section upSection = maybeUpSection.get();
            Station upperStation = upSection.getUpStation();
            list.add(upperStation);
            maybeUpSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(upperStation))
                    .findFirst();
        }
        Collections.reverse(list);
        return list;
    }

    private List<Station> getDownerStations(Station downStation) {
        List<Station> list = new ArrayList<>();
        list.add(downStation);
        Optional<Section> maybeDownSection = sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst();
        while (maybeDownSection.isPresent()) {
            Section downSection = maybeDownSection.get();
            Station downerStation = downSection.getDownStation();
            list.add(downerStation);
            maybeDownSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(downerStation))
                    .findFirst();
        }
        return list;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station) {
        Section section = sections.get(sections.size() - 1);
        Station lastStation = section.getDownStation();
        if (!lastStation.equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }
}
