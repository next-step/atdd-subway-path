package subway.section;

import subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> transferToStations() {
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    public boolean hasUnderOneSection() {
        return sections.size() <= 1;
    }

    public boolean isLastSection(Station station) {
        return lastSection().eqDownStation(station);
    }

    public Section lastSection() {
        return sections.get(sections.size() -1);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }
}
