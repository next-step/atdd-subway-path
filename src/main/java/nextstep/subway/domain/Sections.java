package nextstep.subway.domain;

import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addFirst(Section section) {
        sections.add(0, section);
    }

    public void addLast(Section section) {
        sections.add(section);
    }

    public void addMiddleUpStation(Section section) {
        Section existSection = sections.stream()
                .filter(e -> e.equalUpStation(section.getUpStation()))
                .findAny()
                .orElse(null);
        int index = sections.indexOf(existSection);
        sections.get(index).updateDownStation(section);
        sections.add(index, section);
    }

    public void addMiddleDownStation(Section section) {
        Section existSection = sections.stream()
                .filter(e -> e.equalDownStation(section.getDownStation()))
                .findAny()
                .orElse(null);
        int index = sections.indexOf(existSection);
        sections.get(index).updateUpStation(section);
        sections.add(index + 1, section);
    }

    public boolean containsAtUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    public boolean containsAtDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(downStation));
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());

        return stations;
    }

    public void removeLast(Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.equalDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }
}
