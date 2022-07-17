package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        // 기존에 추가
        sections.stream()
                .filter(item -> item.isSameUpStation(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> {
                    if (section.isAddBetween(newSection.getDistance())) {
                        int index = this.sections.indexOf(section);
                        this.sections.add(index, newSection);
                        section.update(newSection.getDownStation(), newSection.getDistance());
                        return;
                    }
                });

        this.sections.add(newSection);
    }

    public void remove() {
        this.sections.remove(size() - 1);
    }

    public boolean isLastDownStation(Station station) {
        return getLastDownStation().equals(station);
    }

    public List<Station> getStations() {
        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, getFirstUpStation());
        return stations;
    }

    private Station getFirstUpStation() {
        return this.sections.get(0).getUpStation();
    }

    public Station getLastDownStation() {
        return this.sections.get(size() - 1).getDownStation();
    }

    public int size() {
        return this.sections.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
