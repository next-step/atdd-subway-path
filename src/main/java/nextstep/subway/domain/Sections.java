package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        stations.add(firstSection().getUpStation());
        stations.add(firstSection().getDownStation());
    }

    private Station findDownStation(Station upStation) {
        return this.sections.stream().filter(section -> upStation.equals(section.getUpStation())).findFirst().orElseThrow(IllegalArgumentException::new).getDownStation();
    }

    private Station findFirstUpStation() {
        return firstSection().getUpStation();
    }

    private Section firstSection() {
        Section firstSection = this.sections.get(0);
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(firstSection.getUpStation()))
                .findFirst()
                .orElse(firstSection);
    }

    private Section lastSection() {
        Section firstSection = this.sections.get(0);
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(firstSection.getDownStation()))
                .findFirst()
                .orElse(firstSection);
    }

    private boolean isFirstUpStation(Station station) {
        return firstSection().getUpStation().equals(station);
    }

    private boolean hasNextStation(Station prevStation) {
        return this.sections.stream().anyMatch(section -> prevStation.equals(section.getUpStation()));
    }

    private Station findNextStation(Station firstDownStation) {
        return this.sections.stream().filter(section -> firstDownStation.equals(section.getUpStation())).findFirst().map(Section::getDownStation).orElseThrow(() -> new IllegalArgumentException("다음 역이 존재하지 않습니다."));
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
        if (!isSplitSection(upStation)) {
            this.sections.add(of(line, upStation, downStation, distance));
            return;
        }
        addSplitSection(line, upStation, downStation, distance);
    }

    private boolean isFirstAddSection() {
        return this.sections.size() == 0;
    }

    private boolean isSplitSection(Station upStation) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    private void addSplitSection(Line line, Station upStation, Station downStation, int distance) {
        validSplitSectionDistance(upStation, distance);
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
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("겹치는 상행역을 찾을 수 없습니다."));
    }

    public void delete(Station station, Line line) {
        validDeleteSection(line);
        if (isFirstUpStation(station)) {
            deleteFirstStation();
            return;
        }
        if (isLastStation(station)) {
            deleteLastStation();
            return;
        }
        deleteAndMergeMiddleStation(station, line);
    }

    private void validDeleteSection(Line line) {
        if (line.sections().size() == 1) {
            throw new IllegalArgumentException("두 개 이상의 구간일때만 삭제가 가능합니다.");
        }
    }

    private void deleteAndMergeMiddleStation(Station station, Line line) {
        Section previousSection = previousSection(station);
        Section nextSection = nextSection(station);
        deleteSections(previousSection, nextSection);
        mergeSection(line, previousSection, nextSection);
    }

    private void deleteSections(Section previousSection, Section nextSection) {
        sections.remove(previousSection);
        sections.remove(nextSection);
    }

    private void mergeSection(Line line, Section previousSection, Section nextSection) {
        this.sections.add(new Section(line, previousSection.getUpStation(), nextSection.getDownStation(), previousSection.getDistance() + nextSection.getDistance()));
    }

    private Section previousSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행 구간이 존재하지 않습니다."));
    }

    private Section nextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("하행 구간이 존재하지 않습니다."));
    }

    private boolean isLastStation(Station station) {
        Section lastSection = this.sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
        return Objects.equals(lastSection == null ? station : lastSection.getDownStation(), station);
    }

    private void deleteLastStation() {
        this.sections.remove(lastSection());
    }

    private void deleteFirstStation() {
        this.sections.remove(firstSection());
    }

}