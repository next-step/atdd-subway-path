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
        addFirstSection(stations);
        Station firstDownStation = findDownStation(findFirstUpStation());
        addNextStations(stations, firstDownStation);
        return stations;
    }

    private void addNextStations(List<Station> stations, Station firstDownStation) {
        Station prevStation = firstDownStation;
        while (hasNextStation(prevStation)) {
            Station nextStation = findNextStation(prevStation);
            stations.add(nextStation);
            prevStation = nextStation;
        }
    }

    private void addFirstSection(List<Station> stations) {
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        Station firstDownStation = findDownStation(firstUpStation);
        stations.add(firstDownStation);
    }

    private Station findDownStation(Station upStation) {
        for (Section section : this.sections) {
            if (upStation.equals(section.getUpStation())) {
                return section.getDownStation();
            }
        }
        throw new IllegalArgumentException();
    }

    private Station findFirstUpStation() {
        Station firstUpStation = upStation();
        for (Section section : this.sections) {
            if (isDownStation(firstUpStation, section)) {
                firstUpStation = section.getUpStation();
            }
        }
        return firstUpStation;
    }

    private boolean hasNextStation(Station prevStation) {
        return this.sections.stream().anyMatch(section -> prevStation.equals(section.getUpStation()));
    }

    private boolean isDownStation(Station upStation, Section section) {
        return section.getDownStation().equals(upStation);
    }

    private Station findNextStation(Station firstDownStation) {
        return this.sections.stream().filter(section -> firstDownStation.equals(section.getUpStation())).findFirst().map(Section::getDownStation).orElse(null);
    }

    public List<Section> sections() {
        return this.sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (isFirstAddSection()) {
            this.sections.add(of(line, upStation, downStation, distance));
            return;
        }
        validAddSection(upStation, downStation);
        if (!isSplitSection(line, upStation, downStation, distance)) {
            this.sections.add(of(line, upStation, downStation, distance));
            return;
        }
        validSplitSectionDistance(upStation, distance);
        addSplitSection(line, upStation, downStation, distance);
    }

    private boolean isFirstAddSection() {
        return this.sections.size() == 0;
    }

    private boolean isSplitSection(Line line, Station upStation, Station downStation, int distance) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    private void addSplitSection(Line line, Station upStation, Station downStation, int distance) {
        Section sameUpStationSection = sameUpStationSection(upStation);
        this.sections.remove(sameUpStationSection);
        this.sections.add(of(line, upStation, downStation, distance));
        this.sections.add(of(line, downStation, sameUpStationSection.getDownStation(), sameUpStationSection.getDistance() - distance));
    }

    private void validSplitSectionDistance(Station upStation, int distance) {
        if (distance >= sameUpStationSection(upStation).getDistance()) {
            throw new IllegalArgumentException("추가하려는 구간의 길이가 기존 길이보다 같거나 길 수 없습니다.");
        }
    }

    private void validAddSection(Station upStation, Station downStation) {
        validMinimumOneStation(upStation, downStation);
        validMaximumOneStation(upStation, downStation);
    }

    private void validMaximumOneStation(Station upStation, Station downStation) {
        if (stations().contains(upStation) && stations().contains(downStation)) {
            throw new IllegalArgumentException("최대 1개의 역만 노선에 등록되어 있어야 합니다.");
        }
    }

    private void validMinimumOneStation(Station upStation, Station downStation) {
        if (!stations().contains(upStation) && !stations().contains(downStation)) {
            throw new IllegalArgumentException("최소 1개 이상의 역은 노선에 등록되어 있어야 합니다.");
        }
    }

    private Section sameUpStationSection(Station upStation) {
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

    public void deleteSection(Station station) {
        if (!this.downStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

}
