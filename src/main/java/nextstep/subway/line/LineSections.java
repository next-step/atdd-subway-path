package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected LineSections() {
    }


    public boolean exist(long downStationId) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId() == downStationId
                        || section.getDownStationId() == downStationId);
    }


    public Optional<Section> find(long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId() == stationId)
                .findFirst();
    }

    public void remove(Section deleteSection) {
        Section lastSection = this.sections.get(sections.size() - 1);
        if (lastSection.equals(deleteSection)) {
            this.sections = sections.subList(0, sections.size() - 1);
            return;
        }
        throw new IllegalStateException("하행 종점만 삭제 가능");
    }

    public boolean deletable() {
        return this.sections.size() > 1;
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        Set<Station> set = new LinkedHashSet<>();
        for (Section section : sections) {
            set.add(section.getUpStation());
            set.add(section.getDownStation());
        }
        return List.copyOf(set);
    }
}
