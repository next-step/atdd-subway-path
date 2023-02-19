package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {
        if (isEmpty()) {
            sections.add(section);
            return;
        }

        Map<Station, Station> upStationDownStationMap = getUpStationDownStationMap();
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();

        List<Station> stations = getStations(upStationDownStationMap, downStationSectionMap);

        if (stations.containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            throw new IllegalArgumentException("구간의 역이 모두 이미 노선에 존재하는 경우 등록할 수 없습니다.");
        }

        if (addToEitherEnd(section, stations)) {
            return;
        }

        addInTheMiddle(section, upStationDownStationMap, downStationSectionMap);
    }

    private boolean addToEitherEnd(Section section, List<Station> stations) {
        int lastIndex = stations.size() - 1;
        if (stations.get(0).equals(section.getDownStation()) ||
            stations.get(lastIndex).equals(section.getUpStation())) {
            sections.add(section);
            return true;
        }
        return false;
    }

    private void addInTheMiddle(Section section, Map<Station, Station> upStationDownStationMap,
        Map<Station, Section> downStationSectionMap) {
        Section foundSection = null;
        if (upStationDownStationMap.containsKey(section.getUpStation())) {
            foundSection = downStationSectionMap.get(
                upStationDownStationMap.get(section.getUpStation()));
        }

        if (downStationSectionMap.containsKey(section.getDownStation())) {
            foundSection = downStationSectionMap.get(section.getDownStation());
        }

        if (foundSection == null) {
            throw new IllegalArgumentException("구간의 역 중 하나는 기존 노선에 존재해야합니다.");
        }

        foundSection.updateWhenSectionAddedInMiddle(section);
        sections.add(section);
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return new ArrayList<>();
        }

        Map<Station, Station> upStationDownStationMap = getUpStationDownStationMap();
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();

        return getStations(upStationDownStationMap, downStationSectionMap);
    }

    private List<Station> getStations(Map<Station, Station> upStationDownStationMap,
        Map<Station, Section> downStationSectionMap) {
        Station firstStation = getFirstStation(upStationDownStationMap, downStationSectionMap);

        return getOrderedStations(upStationDownStationMap, firstStation);
    }

    private Station getFirstStation(Map<Station, Station> upStationDownStationMap,
        Map<Station, Section> downStationSectionMap) {
        return upStationDownStationMap.keySet().stream()
            .filter(key -> !downStationSectionMap.containsKey(key))
            .findFirst()
            .orElseThrow(RuntimeException::new);
    }

    private List<Station> getOrderedStations(Map<Station, Station> upStationDownStationMap,
        Station firstStation) {
        List<Station> stations = new ArrayList<>();

        Station station = firstStation;
        while (station != null) {
            stations.add(station);
            station = upStationDownStationMap.get(station);
        }

        return stations;
    }

    public void remove(Station station) {
        int lastIndex = sections.size() - 1;
        if (!sections.get(lastIndex).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastIndex);
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    private Map<Station, Section> getDownStationSectionMap() {
        return sections.stream().collect(Collectors.toMap(Section::getDownStation, s -> s));
    }

    private Map<Station, Station> getUpStationDownStationMap() {
        return sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }
}
