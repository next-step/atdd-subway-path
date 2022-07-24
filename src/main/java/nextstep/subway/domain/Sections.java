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
        if (this.sections.size() > 0) {
            return this.sections.get(this.sections.size() - 1).getDownStation();
        }
        throw new IllegalArgumentException();
    }

    public Station upStation() {
        if (this.sections.size() > 0) {
            return this.sections.get(0).getUpStation();
        }
        throw new IllegalArgumentException();
    }

    public List<Station> stations() {
            List<Station> stations = new ArrayList<>();
        addUpStations(stations);
        addDownStations(stations, getFirstDownStation());
        return stations;
    }

    private Station getFirstDownStation() {
        Station upStation = null;
        int num = 0;
        for (int i = 0; i < this.sections.size(); i++) {
            if (upStation == null) {
                upStation = this.sections.get(i).getUpStation();
                num = i;
            } else if (this.sections.get(i).getDownStation().equals(upStation)) {
                upStation = sections.get(i).getUpStation();
                num = i;
            }
        }
        return this.sections.get(num).getDownStation();
    }

    private void addUpStations(List<Station> stations) {
        Station upStation = null;
        for (int i = 0; i < this.sections.size(); i++) {
            if (upStation == null) {
                upStation = upStation = sections.get(i).getUpStation();
            } else if (this.sections.get(i).getDownStation().equals(upStation)) {
                upStation = sections.get(i).getUpStation();
            }
        }
        stations.add(upStation);
    }

    private void addDownStations(List<Station> stations, Station firstDownStation) {
        stations.add(firstDownStation);
        Station downStation = firstDownStation;
        for (Section section : this.sections) {
            if (downStation.equals(section.getUpStation())) {
                stations.add(section.getDownStation());
                downStation = section.getDownStation();
            }
        }
    }

    public List<Section> sections() {
        return this.sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (this.sections.size() > 0) {
            if (isSameUpStationRoof(line, upStation, downStation, distance)) {
                sectionDeplicateUpStation(line, upStation, downStation, distance);
                splitLine(line, upStation, downStation, distance, sectionDeplicateUpStation(line, upStation, downStation, distance));
            } else {
                this.sections.add(Section.of(line, upStation, downStation, distance));
            }
        } else {
            this.sections.add(Section.of(line, upStation, downStation, distance));
        }
    }

    private Section sectionDeplicateUpStation(Line line, Station upStation, Station downStation, int distance) {
        return this.sections.stream().filter(section -> section.getUpStation().equals(upStation)).findFirst().orElse(null);
    }

    private boolean isSameUpStationRoof(Line line, Station upStation, Station downStation, int distance) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    private void splitLine(Line line, Station upStation, Station downStation, int distance, Section section) {
        Station tempUpStation = section.getDownStation();
        Station tempDownStation = section.getDownStation();
        this.sections.remove(section);
        this.sections.add(Section.of(line, upStation, downStation, distance));
        int tempDistance = section.getDistance();
        this.sections.add(Section.of(line, downStation, tempDownStation, tempDistance - distance));
    }

    private boolean idSameUpstation(Station upStation, int i) {
        return this.sections.get(i).getUpStation().equals(upStation);
    }

    private boolean checkDistance(int distance, int i) {
        return distance < this.sections.get(i).getDistance();
    }

    public void deleteSection(Station station) {
        if (!this.downStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

}
