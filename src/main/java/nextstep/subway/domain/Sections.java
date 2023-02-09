package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void add(Section Section) {
        sections.add(Section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getLastSection() {
        return sections.get(getLastIndex());
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
}
