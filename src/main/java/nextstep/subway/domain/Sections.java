package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public int size() {
        return sections.size();
    }

    public void add(Section newSection) {
        if (!sections.isEmpty()) {
            Section findSection = sections.stream().filter(s -> s.isUpStation(newSection.getUpStation()))
                    .findAny().orElseThrow(IllegalArgumentException::new);
            findSection.changeUpStation(newSection.getDownStation());
            findSection.subtractDistance(newSection.getDistance());
        }
        sections.add(newSection);
    }

    public List<Station> getStations() {
        Station upEndStation = getUpEndStation();

        List<Station> findStations = new ArrayList<>();
        findStations.add(upEndStation);

        Stack<Station> upStationStack = new Stack<>();
        upStationStack.push(upEndStation);

        while (!upStationStack.isEmpty()) {
            Station currentStation = upStationStack.pop();
            sections.stream()
                    .filter(section -> section.isUpStation(currentStation))
                    .findAny()
                    .ifPresent(section -> {
                        Station nextStation = section.getDownStation();
                        findStations.add(nextStation);
                        upStationStack.push(nextStation);
                    });
        }

        return findStations;
    }

    public void remove(Section... section) {
        sections.removeAll(List.of(section));
    }

    public void removeLastSection(Station station) {
        if (sections.isEmpty() || !sections.get(sections.size() - 1).isDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.get(sections.size() - 1));
    }

    private Station getUpEndStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> sections.stream().noneMatch(section -> section.isDownStation(upStation)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
