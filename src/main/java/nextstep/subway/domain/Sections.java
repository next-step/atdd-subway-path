package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteSection(Long stationId) {
        if (!isDownMostStation(stationId)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public boolean isDownMostStation(Long stationId) {
        Section lastSection = sections.get(sections.size() - 1);
        return lastSection.isSameDownStation(stationId);
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        if (!stations.isEmpty()) {
            stations.add(0, sections.get(0).getUpStation());
        }

        return stations;
    }

    public List<Section> getValue() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
