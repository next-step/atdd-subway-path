package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    Sections(List<Section> sections) {
        this.sections = sections;
    }


    private Map<Station, Station> getStationMap() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private Station getStartingStation() {
        Map<Station, Station> stationMap = getStationMap();
        Set<Station> downStationSet = new HashSet<>(stationMap.values());

        return stationMap.keySet().stream()
                .filter(Predicate.not(downStationSet::contains))
                .findAny().orElseThrow(IllegalStateException::new);
    }

    private Station getEndingStation() {
        Map<Station, Station> stationMap = getStationMap();
        Set<Station> upStationSet = stationMap.keySet();

        return stationMap.values().stream()
                .filter(Predicate.not(upStationSet::contains))
                .findAny().orElseThrow(IllegalStateException::new);
    }

    public List<Section> get() {
        return sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        Station startingStation = getStartingStation();
        Map<Station, Station> stationMap = getStationMap();

        List<Station> stations = new ArrayList<>();
        stations.add(startingStation);
        while (stationMap.containsKey(startingStation)) {
            startingStation = stationMap.get(startingStation);
            stations.add(startingStation);
        }
        return stations;
    }

    public void remove(Station station) {
        Station endingStation = getEndingStation();
        if (!endingStation.equals(station)) {
            throw new IllegalArgumentException("구간이 목록에서 마지막 역이 아닙니다.");
        }

        Section section = sections.stream()
                .filter(_section -> _section.getDownStation().equals(station))
                .findAny().orElseThrow(() -> new IllegalArgumentException("구간에 존재하지 않는 역입니다."));
        sections.remove(section);
    }
}
