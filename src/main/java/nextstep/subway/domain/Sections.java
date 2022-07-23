package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Station downStation() {
        if (this.sections.size() > 0)
            return this.sections.get(this.sections.size() - 1).getDownStation();
        throw new IllegalArgumentException();
    }

    public Station upStation() {
        if (this.sections.size() > 0)
            return this.sections.stream().findFirst().get().getUpStation();
        throw new IllegalArgumentException();
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        addUpStations(stations);
        addDownStations(stations);
        return stations;
    }

    private void addUpStations(List<Station> stations) {
        stations.add(this.sections.get(0).getUpStation());
    }

    private void addDownStations(List<Station> stations) {
        for (Section section : this.sections) {
            stations.add(section.getDownStation());
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
