package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class LineSection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void remove(String stationName) {
        sections.removeIf(v->v.getUpStation().getName().equals(stationName));
    }
    public void removeLast() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }

    public int size() {
        return sections.size();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
    public void checkArgument(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
    }
}
