package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OrderBy("upStation.id ASC")
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        this.values.add(section);
    }

    public List<Station> getAllStations() {
        Station firstStation = getFirstStation();
        List<Station> downStations = getDownStations();
        List<Station> stations = new ArrayList<>();
        stations.add(firstStation);
        stations.addAll(downStations);
        return stations;
    }

    public Station getFirstStation() {
        return values.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."))
                .getUpStation();
    }

    public Station getLastStation() {
        return values.stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."))
                .getDownStation();
    }

    private List<Station> getDownStations() {
        return values.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public Section getLastSection() {
        return values.stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."));
    }

    public void addSection(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        validateDuplicateStations(upStation, downStation);

        if (getFirstStation().isSameStation(downStation)) {
            values.add(0, section);
            return;
        }

        if (getLastStation().isSameStation(upStation)) {
            values.add(section);
            return;
        }

        for (int index = 0; index < values.size(); index++) {
            if (tryAddSectionInMiddle(section, index)) return;
        }

        throw new IllegalArgumentException("새로운 구간을 추가할 수 있는 연결점이 없습니다. upStationId: " + upStation.getId() + ", downStationId: " + downStation.getId());
    }

    private boolean tryAddSectionInMiddle(Section section, int matchedIndex) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Section currentSection = values.get(matchedIndex);

        if (currentSection.isMatchWithUpStation(upStation)) {
            Section matchedSection = values.remove(matchedIndex);
            matchedSection.updateUpStation(downStation);
            values.add(matchedIndex, section);
            values.add(matchedIndex + 1, matchedSection);
            return true;
        }

        if (currentSection.isMatchWithDownStation(downStation)) {
            Section matchedSection = values.remove(matchedIndex);
            matchedSection.updateDownStation(upStation);
            values.add(matchedIndex, matchedSection);
            values.add(matchedIndex + 1, section);
            return true;
        }
        return false;
    }

    public void removeStation(Station station) {
        validateRemovableLastSection(station);
        values.removeIf(value -> value.containStation(station));
    }

    private void validateDuplicateStations(Station upStation, Station downStation) {
        boolean hasUpStation = values.stream().anyMatch(value -> value.containStation(upStation));
        boolean hasDownStation = values.stream().anyMatch(value -> value.containStation(downStation));
        if (hasDownStation && hasUpStation) {
            throw new IllegalArgumentException("주어진 구간은 이미 노선에 등록되어 있는 구간입니다. upStationId: " + upStation.getId() + ", downStationId: " + downStation.getId());
        }
    }

    public void validateRemovableLastSection(Station station) {
        validateLatestSection(station);
        validateSize();
    }

    private void validateLatestSection(Station station) {
        Section lastSection = getLastSection();
        if (lastSection.getDownStation().isNotSameStation(station)) {
            throw new IllegalArgumentException("노선의 하행 종착역만 삭제할 수 있습니다. stationId: " + station.getId());
        }
    }

    private void validateSize() {
        if (values.size() < 2) {
            throw new IllegalArgumentException("노선에 남은 구간이 1개뿐이라 삭제할 수 없습니다.");
        }
    }
}
