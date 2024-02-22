package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

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
        validateLastStation(section);
        validateDuplicateStation(section);
        values.add(section);
    }

    public void removeStation(Station station) {
        validateRemovableLastSection(station);
        values.removeIf(value -> value.containStation(station));
    }

    private void validateLastStation(Section section) {
        Station lastStation = getLastSection().getDownStation();
        Station upStation = section.getUpStation();
        if (lastStation.isNotSameStation(upStation)) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 노선의 하행 종착역과 같아야 합니다. upStationId: " + upStation.getId());
        }
    }

    private void validateDuplicateStation(Section section) {
        Station downStation = section.getDownStation();
        if (values.stream().anyMatch(value -> value.containStation(downStation))) {
            throw new IllegalArgumentException("주어진 하행역은 이미 노선에 등록되어 있는 등록된 역입니다. downStationId: " + downStation.getId());
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
