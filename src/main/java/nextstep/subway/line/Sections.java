package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        this.values.add(section);
    }

    public List<Station> getAllStations() {
        sortSections();
        List<Station> stations = new ArrayList<>();
        stations.add(getFirstStation());
        values.forEach(section -> stations.add(section.getDownStation()));
        return stations;
    }

    private Station getFirstStation() {
        return values.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."))
                .getUpStation();
    }

    private Station getLastStation() {
        return values.stream()
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."))
                .getDownStation();
    }

    public void addSection(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        validateDuplicateStations(upStation, downStation);
        sortSections();

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

    private void sortAllSections(List<Section> sortedSections) {
        while (!values.isEmpty()) {
            Section currentSection = sortedSections.get(sortedSections.size() - 1);
            Station currentDownStation = currentSection.getDownStation();

            Section nextSection = values.stream()
                    .filter(section -> section.getUpStation().isSameStation(currentDownStation))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."));

            sortedSections.add(nextSection);
            values.remove(nextSection);
        }
    }

    private void sortFirstSection(List<Section> sortedSections) {
        Section firstSection = values.stream()
                .filter(section -> values.stream().noneMatch(other -> section.getUpStation().isSameStation(other.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."));

        sortedSections.add(firstSection);
        values.remove(firstSection);
    }

    private boolean tryAddSectionInMiddle(Section section, int matchedIndex) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Section currentSection = values.get(matchedIndex);

        if (currentSection.isMatchWithUpStation(upStation)) {
            Section matchedSection = values.remove(matchedIndex);
            matchedSection.updateUpStationAndDecreaseDistance(downStation, section.getDistance());
            values.add(matchedIndex, section);
            values.add(matchedIndex + 1, matchedSection);
            return true;
        }

        if (currentSection.isMatchWithDownStation(downStation)) {
            Section matchedSection = values.remove(matchedIndex);
            matchedSection.updateDownStationAndDecreaseDistance(upStation, section.getDistance());
            values.add(matchedIndex, matchedSection);
            values.add(matchedIndex + 1, section);
            return true;
        }
        return false;
    }

    private void validateDuplicateStations(Station upStation, Station downStation) {
        boolean hasUpStation = values.stream().anyMatch(value -> value.hasStation(upStation));
        boolean hasDownStation = values.stream().anyMatch(value -> value.hasStation(downStation));
        if (hasDownStation && hasUpStation) {
            throw new IllegalArgumentException("주어진 구간은 이미 노선에 등록되어 있는 구간입니다. upStationId: " + upStation.getId() + ", downStationId: " + downStation.getId());
        }
    }

    public void removeStation(Station station) {
        validateSize();
        sortSections();

        if (isStartOrEndStation(station)) {
            removeStartOrEndSection(station);
            return;
        }
        if (isMiddleStation(station)) {
            removeMiddleSection(station);
            return;
        }

        throw new IllegalArgumentException("노선에 구간이 존재하지 않습니다. stationId: " + station.getId());
    }

    private void validateSize() {
        if (values.size() < 2) {
            throw new IllegalArgumentException("노선에 남은 구간이 1개뿐이라 제거할 수 없습니다.");
        }
    }

    private void sortSections() {
        List<Section> sortedSections = new ArrayList<>();
        sortFirstSection(sortedSections);
        sortAllSections(sortedSections);
        values.addAll(sortedSections);
    }

    private boolean isStartOrEndStation(Station station) {
        return values.stream()
                .filter(section -> section.hasStation(station))
                .count() == 1;
    }

    private boolean isMiddleStation(Station station) {
        return values.stream()
                .filter(section -> section.hasStation(station))
                .count() == 2;
    }

    private void removeStartOrEndSection(Station station) {
        values.removeIf(section -> section.hasStation(station));
    }

    private void removeMiddleSection(Station station) {
        Section sectionByDownStation = getSectionByDownStation(station);
        Section sectionByUpStation = getSectionByUpStation(station);
        values.removeIf(section -> section.isMatchWithUpStation(station));
        sectionByDownStation.updateDownStationAndIncreaseDistance(sectionByUpStation);
    }

    private Section getSectionByDownStation(Station station) {
        return values.stream()
                .filter(section -> section.isMatchWithDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 역이 존재하지 않습니다. stationId: " + station.getId()));
    }

    private Section getSectionByUpStation(Station station) {
        return values.stream()
                .filter(section -> section.isMatchWithUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 역이 존재하지 않습니다. stationId: " + station.getId()));
    }
}
