package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Function;
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

    private Optional<Section> findSectionByStation(Station station, Function<Section, Station> sectionToStationMapper) {
        return sections.stream().filter(it -> sectionToStationMapper.apply(it).equals(station)).findAny();
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return findSectionByStation(upStation, Section::getUpStation);
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return findSectionByStation(downStation, Section::getDownStation);
    }

    private void replaceSection(Section replacedSection, Station newSectionUpStation, Station newSectionDownStation, int remainedDistance) {
        final int minDistanceExclusive = 0;
        if (remainedDistance <= minDistanceExclusive) {
            throw new IllegalArgumentException("기존 구간 거리 이상 구간은 사이에 추가할 수 없습니다.");
        }
        sections.remove(replacedSection);
        sections.add(new Section(replacedSection.getLine(), newSectionUpStation, newSectionDownStation, remainedDistance));
    }

    public void add(Section section) {
        Station addedUpStation = section.getUpStation();
        Station addedDownStation = section.getDownStation();

        if (sections.isEmpty() || getStartingStation().equals(addedDownStation) ^ getEndingStation().equals(addedUpStation)) {
            sections.add(section);
            return;
        }

        Optional<Section> upStationSection = findSectionByUpStation(addedUpStation);
        Optional<Section> downStationSection = findSectionByDownStation(addedDownStation);

        if (upStationSection.isEmpty() && downStationSection.isEmpty()) {
            throw new IllegalArgumentException("기존 구간에 속하지 않는 역 만으로 구간의 상행, 하행 역을 지정할 수 없습니다.");
        }
        if (upStationSection.isPresent() && downStationSection.isPresent()) {
            throw new IllegalArgumentException("기존 구간에 속하는 역 만으로 구간의 상행, 하행 역을 지정할 수 없습니다.");
        }

        upStationSection.ifPresent(it -> replaceSection(
                it, section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()
        ));
        downStationSection.ifPresent(it -> replaceSection(
                it, it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()
        ));

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
        Optional<Section> downStationSection = findSectionByDownStation(station);
        Optional<Section> upStationSection = findSectionByUpStation(station);

        if (downStationSection.isEmpty() && upStationSection.isEmpty()) {
            throw new IllegalArgumentException("구간에 존재하지 않는 역입니다.");
        }

        final int sectionMinCountInclusive = 1;
        if (sections.size() <= sectionMinCountInclusive) {
            throw new IllegalArgumentException("구간이 한 개면 삭제할 수 없습니다.");
        }

        if (downStationSection.isPresent() && upStationSection.isPresent()) {
            sections.add(mergeSection(downStationSection.get(), upStationSection.get()));
        }
        upStationSection.ifPresent(sections::remove);
        downStationSection.ifPresent(sections::remove);
    }

    private Section mergeSection(Section upSection, Section downSection) {
        return new Section(
                upSection.getLine(),
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance()
        );
    }

    public void registerToPathFinder(PathFinder pathFinder) {
        sections.forEach(section -> pathFinder.registerSection(section.getUpStation(), section.getDownStation(), section.getDistance()));
    }
}
