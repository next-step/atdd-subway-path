package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import lombok.Getter;
import org.springframework.data.annotation.Transient;

@Embeddable
public class Sections {
    @Getter
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Transient
    private final String FIRST = "FIRST";
    @Transient
    private final String LAST = "LAST";
    @Transient
    private final String MIDDLE = "MIDDLE";

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);

        Map<Station, Station> upStationDownStationMap = getUpStationDownStationMap();
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();
        List<Station> stations = getStations(upStationDownStationMap, downStationSectionMap);

        if (stations.containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            throw new IllegalArgumentException("구간의 역이 모두 이미 노선에 존재하는 경우 등록할 수 없습니다.");
        }

        if (isNewSectionAtEitherEnd(section, stations)) {
            sections.add(section);
            return;
        }

        Section foundSection = findExistSectionRelatedNewSection(section, upStationDownStationMap,
            downStationSectionMap)
            .orElseThrow(() -> new IllegalArgumentException("구간의 역 중 하나는 기존 노선에 존재해야합니다."));
        addInTheMiddle(section, foundSection);
    }

    private boolean isNewSectionAtEitherEnd(Section section, List<Station> stations) {
        if (isEmpty()) {
            return true;
        }

        int lastIndex = stations.size() - 1;
        return stations.get(0).equals(section.getDownStation()) ||
            stations.get(lastIndex).equals(section.getUpStation());
    }

    private Optional<Section> findExistSectionRelatedNewSection(Section section,
        Map<Station, Station> upStationDownStationMap,
        Map<Station, Section> downStationSectionMap) {
        Optional<Section> foundSection = Optional.empty();

        if (upStationDownStationMap.containsKey(section.getUpStation())) {
            foundSection = Optional.ofNullable(downStationSectionMap.get(
                upStationDownStationMap.get(section.getUpStation())));
        }

        if (downStationSectionMap.containsKey(section.getDownStation())) {
            foundSection = Optional.ofNullable(downStationSectionMap.get(section.getDownStation()));
        }
        return foundSection;
    }

    private void addInTheMiddle(Section section, Section foundSection) {
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
        Optional<Station> optionalStation =
            getFirstStation(upStationDownStationMap, downStationSectionMap);
        if (optionalStation.isEmpty()) {
            return new ArrayList<>();
        }
        return getOrderedStations(upStationDownStationMap, optionalStation.get());
    }

    private Optional<Station> getFirstStation(Map<Station, Station> upStationDownStationMap,
        Map<Station, Section> downStationSectionMap) {
        return upStationDownStationMap.keySet().stream()
            .filter(key -> !downStationSectionMap.containsKey(key))
            .findFirst();
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
        int stationCountLimitForRemove = 2;
        if (sections.size() < stationCountLimitForRemove) {
            throw new IllegalArgumentException("구간이 두개 미만이면 삭제 할 수 없습니다.");
        }

        Map<Station, Station> upStationDownStationMap = getUpStationDownStationMap();
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();

        List<Station> stations = getStations(upStationDownStationMap, downStationSectionMap);
        String location = findLocationOfStation(stations, station, downStationSectionMap);

        if (location.equals(FIRST)) {
            sections.remove(downStationSectionMap.get(upStationDownStationMap.get(station)));
            return;
        }

        if (location.equals(LAST)) {
            sections.remove(downStationSectionMap.get(station));
            return;
        }

        if (location.equals(MIDDLE)) {
            Section downStationSection = downStationSectionMap.get(station);
            Section upStationSection = downStationSectionMap
                .get(upStationDownStationMap.get(station));
            mergeSections(downStationSection, upStationSection);
            return;
        }

        throw new EntityNotFoundException("노선에 해당 역이 존재하지 않습니다.");
    }

    private String findLocationOfStation(List<Station> stations, Station station,
        Map<Station, Section> downStationSectionMap) {
        if (stations.get(0).equals(station)) {
            return FIRST;
        }
        int lastIndex = stations.size() - 1;
        if (stations.get(lastIndex).equals(station)) {
            return LAST;
        }
        if (downStationSectionMap.containsKey(station)) {
            return MIDDLE;
        }
        throw new EntityNotFoundException("노선에 해당 역이 존재하지 않습니다.");
    }

    private void mergeSections(Section downStationSection, Section upStationSection) {
        sections.add(new Section(downStationSection.getLine(),
            downStationSection.getUpStation(), upStationSection.getDownStation(),
            downStationSection.getDistance() + upStationSection.getDistance()));
        sections.remove(downStationSection);
        sections.remove(upStationSection);
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
