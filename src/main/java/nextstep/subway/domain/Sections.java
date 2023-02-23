package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;

@Embeddable
@Getter
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (isNotContain(section)) {
            sections.add(section);
        }
    }

    public int size() {
        return sections.size();
    }

    public void removeStation(Station station) {
        if (isNotRemovable(station)) {
            throw new IllegalStateException("Cannot Remove Station");
        }

        sections.remove(sections.size() - 1);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public Station getLastStation() {
       return getLastSection().getDownStation();
    }

    public List<Station> getStations() {
        if(isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        stations.add(getLastStation());

        return stations;
    }

    private boolean isNotContain(Section section) {
        return !sections.contains(section);
    }

    private  Section getLastSection() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("Empty Section");
        }

        return sections.get(sections.size() - 1);
    }

    private boolean isNotRemovable(Station station) {
        return !isRemovable(station);
    }

    private boolean isRemovable(Station station) {
        return getLastStation().equals(station);
    }
}
