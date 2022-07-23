package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Station downStation() {
        if (this.sections.size() > 0)
            return this.sections.get(this.sections.size() - 1).getDownStation();
        return null;
    }

    public Station upStation() {
        if (this.sections.size() > 0)
            return this.sections.stream().findFirst().get().getUpStation();
        return null;
    }

    public Set<Station> stations() {
        Set<Station> stations = new HashSet<>();
        addUpStations(stations);
        addDownStations(stations);
        return stations;
    }

    private void addDownStations(Set<Station> stations) {
        for (Section section : this.sections) {
            stations.add(section.getDownStation());
        }
    }

    private void addUpStations(Set<Station> stations) {
        for (Section section : this.sections) {
            stations.add(section.getUpStation());
        }
    }

    public List<Section> sections() {
        return this.sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(Section.of(line, upStation, downStation, distance));
    }

    public void deleteSection(Station station) {
        if (!this.downStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

}
