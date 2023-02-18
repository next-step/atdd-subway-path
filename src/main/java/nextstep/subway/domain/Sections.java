package nextstep.subway.domain;

import java.util.ArrayList;
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

        Section foundSection = null;

        Map<Station, Section> upStationSectionMap = getUpStationSectionMap();
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();

        if ((upStationSectionMap.containsKey(section.getUpStation()) ||
            downStationSectionMap.containsKey(section.getUpStation())) &&
            (upStationSectionMap.containsKey(section.getDownStation()) ||
            downStationSectionMap.containsKey(section.getDownStation()))) {
            throw new IllegalArgumentException("구간의 역이 모두 이미 노선에 존재하는 경우 등록할 수 없습니다.");
        }

        if (upStationSectionMap.containsKey(section.getUpStation())) {
            foundSection = upStationSectionMap.get(section.getUpStation());
        } else if (downStationSectionMap.containsKey(section.getUpStation())) {  // 하행종점
            sections.add(section);
            return;
        }

        if (downStationSectionMap.containsKey(section.getDownStation())) {
            foundSection = downStationSectionMap.get(section.getDownStation());
        } else if (upStationSectionMap.containsKey(section.getDownStation())) { // 상행종점
            sections.add(section);
            return;
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

    private Map<Station, Section> getUpStationSectionMap() {
        return sections.stream().collect(Collectors.toMap(Section::getUpStation, s -> s));
    }

    private Map<Station, Section> getDownStationSectionMap() {
        return sections.stream().collect(Collectors.toMap(Section::getDownStation, s -> s));
    }

    private Map<Station, Station> getUpStationDownStationMap() {
        return sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }
}
