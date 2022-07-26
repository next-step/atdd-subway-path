package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.domain.Section.of;

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
        for (Section section : this.sections) {
            if (upStation == null || section.getDownStation().equals(upStation)) {
                upStation = section.getUpStation();
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
            validMinimumOneStation(upStation, downStation);
            validMaximumOneStation(upStation, downStation);
            if (isSameUpStation(line, upStation, downStation, distance)) {
                splitSection(line, upStation, downStation, distance, sameUpStationSection(line, upStation, downStation, distance));
            } else {
                this.sections.add(of(line, upStation, downStation, distance));
            }
        } else {
            this.sections.add(of(line, upStation, downStation, distance));
        }
    }

    private void validMaximumOneStation(Station upStation, Station downStation) {
        assert !stations().contains(upStation) || !stations().contains(downStation) : "최대 1개의 역만 노선에 등록되어 있어야 합니다.";
    }

    private void validMinimumOneStation(Station upStation, Station downStation) {
        assert stations().contains(upStation) || stations().contains(downStation) : "최소 1개 이상의 역은 노선에 등록되어 있어야 합니다.";
    }

    private Section sameUpStationSection(Line line, Station upStation, Station downStation, int distance) {
        for (Section section : this.sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }
        throw new IllegalArgumentException("겹치는 상행역을 찾을 수 없습니다.");
    }

    private boolean isSameUpStation(Line line, Station upStation, Station downStation, int distance) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    private void splitSection(Line line, Station upStation, Station downStation, int distance, Section section) {
        Station beforeDownStation = section.getDownStation();
        int beforeDistance = section.getDistance();
        this.sections.remove(section);
        this.sections.add(of(line, upStation, downStation, distance));
        validSectionDistance(distance, beforeDistance);
        this.sections.add(of(line, downStation, beforeDownStation, beforeDistance - distance));
    }

    private void validSectionDistance(int distance, int tempDistance) {
        assert distance < tempDistance : "추가하려는 구간의 길이가 기존 길이보다 같거나 길 수 없습니다.";
    }

    public void deleteSection(Station station) {
        if (!this.downStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

}
