package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void remove() {
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        stations.add(sections.get(0).getUpStation());
        for (Section section: sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public int count() {
        return sections.size();
    }

    public Station getFirstUpStation() {
        return sections.get(0).getUpStation();
    }

    public Station getLastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
