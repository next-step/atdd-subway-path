package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void addLast(Section Section) {
        sections.add(Section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getLastSection() {
        return sections.get(getLastIndex());
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        LinkedList<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toCollection(LinkedList::new));
        stations.addFirst(sections.get(0).getUpStation());
        return stations;
    }

    public void removeLastSection() {
        sections.remove(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public boolean checkExistStation(Station station) {
        return getStations().stream()
                .anyMatch(s -> s.equals(station));
    }

    public boolean containsStations(List<Station> stations) {
        List<Station> existStations = getStations();
        return new HashSet<>(existStations).containsAll(stations);
    }

    public void addFirst(Section section) {
        sections.add(0, section);
    }
}
