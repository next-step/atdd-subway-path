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

    public List<Section> sections() {
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
        return lastSection().equalDownStation(station);
    }

    public Section lastSection() {
        return sections.get(sections.size() -1);
    }

    public void add(Section section) {
        if (!sections.isEmpty()) {
            if (!isLastSection(section.getUpStation())) {
                throw new IllegalArgumentException("상행역이 하행종점역과 같지 않습니다.");
            }
        }

        sections.add(section);
    }

    public void remove(Section section) {
        sections.remove(section);
    }
}
