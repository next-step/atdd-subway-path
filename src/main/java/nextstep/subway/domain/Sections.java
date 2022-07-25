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
            if (!stations().contains(upStation) && !stations().contains(downStation)) {
                throw new IllegalArgumentException("둘 중 하나라도");
            }
            if (stations().contains(upStation) && stations().contains(downStation)) {
                throw new IllegalArgumentException("둘 다 포함");
            }
            if (isSameUpStationRoof(line, upStation, downStation, distance)) {
                splitLine(line, upStation, downStation, distance, sectionDeplicateUpStation(line, upStation, downStation, distance));
            } else {
                this.sections.add(Section.of(line, upStation, downStation, distance));
            }
        } else {
            this.sections.add(Section.of(line, upStation, downStation, distance));
        }
    }

    private Section sectionDeplicateUpStation(Line line, Station upStation, Station downStation, int distance) {
        for (Section section : this.sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }
        return null;
    }

    private boolean isSameUpStationRoof(Line line, Station upStation, Station downStation, int distance) {
        for (Section section : this.sections) {
            if (section.getUpStation().equals(upStation)) {
                return true;
            }
        }
        return false;
    }

    private void splitLine(Line line, Station upStation, Station downStation, int distance, Section section) {
        Station tempUpStation = section.getDownStation();
        Station tempDownStation = section.getDownStation();
        this.sections.remove(section);
        this.sections.add(Section.of(line, upStation, downStation, distance));
        int tempDistance = section.getDistance();
        if (distance >= tempDistance) {
            throw new IllegalArgumentException("길이 오류");
        }
        this.sections.add(Section.of(line, downStation, tempDownStation, tempDistance - distance));
    }

    public void deleteSection(Station station) {
        if (!this.downStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

}
