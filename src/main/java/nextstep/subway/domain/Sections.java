package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if(sections.isEmpty()){
            sections.add(newSection);
            return;
        }

        validateAddSectionRule(newSection);

        List<Station> sortedStations = getSortedStations();
        Station lastUpStation = sortedStations.get(0);
        Station lastDownStation = sortedStations.get(sortedStations.size()-1);

        if(canAddSectionToUpOrDown(newSection, lastUpStation, lastDownStation)){
            sections.add(newSection);
            return;
        }

        addSectionToMiddle(newSection);
    }

    private void addSectionToMiddle(Section newSection) {
        Section targetSection =
                sections.stream()
                        .filter(
                                e -> e.equalsUpStation(newSection.getUpStation())
                                        || e.equalsDownStation(newSection.getDownStation()))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);


        if (targetSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("추가된 구간이 기존 구간보다 길이가 깁니다.");
        }

        sections.add(newSection);


        if (targetSection.equalsUpStation(newSection.getUpStation())) {
            targetSection.reduceDistance(newSection.getDistance());
            targetSection.updateUpStation(newSection.getDownStation());
        } else {
            targetSection.reduceDistance(newSection.getDistance());
            targetSection.updateDownStation(newSection.getUpStation());
        }
    }

    private boolean canAddSectionToUpOrDown(Section newSection, Station lastUpStation, Station lastDownStation) {
        return newSection.equalsUpStation(lastDownStation) || newSection.equalsDownStation(lastUpStation);
    }

    private void validateAddSectionRule(Section newSection) {
        if (isNegativeDistance(newSection.getDistance())) {
            throw new IllegalArgumentException("구간의 거리는 0보다 커야합니다");
        }

        if (isAllStationExist(newSection)) {
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 이미 노선에 포함되어 있습니다.");
        }

        if (isAllStationNotExist(newSection)) {
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 노선에 포함되어 있지 않습니다.");
        }
    }

    private boolean isAllStationExist(Section newSection) {
        List<Station> stations = getStations();

        return stations.contains(newSection.getUpStation()) && stations.contains(newSection.getDownStation());
    }

    private boolean isAllStationNotExist(Section newSection) {
        List<Station> stations = getStations();

        return !stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation());
    }

    public void deleteSection(Station station) {
        List<Station> sortedStations = getSortedStations();

        validateDeleteSectionRule(sortedStations, station);

        if(isLastUpOrDownStation(sortedStations, station)){
            deleteUpOrDownSection(station);
            return;
        }

        deleteMiddleSection(station);
    }

    private void deleteMiddleSection(Station targetStation) {
        Section targetUpSection = sections.stream()
                .filter(e -> e.equalsDownStation(targetStation))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);

        Section targetDownSection = sections.stream()
                                            .filter(e -> e.equalsUpStation(targetStation))
                                            .findFirst()
                                            .orElseThrow(IllegalAccessError::new);

        targetUpSection.addDistance(targetDownSection.getDistance());
        targetUpSection.updateDownStation(targetDownSection.getDownStation());

        sections.remove(targetDownSection);
    }

    private void deleteUpOrDownSection(Station targetStation) {
        Section targetSection = sections.stream()
                .filter(e -> e.equalsUpStation(targetStation)
                        || e.equalsDownStation(targetStation))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);

        sections.remove(targetSection);
    }

    private void validateDeleteSectionRule(List<Station> sortedStations, Station targetStation){
        if(sections.isEmpty()){
            throw new IllegalArgumentException("노선에 구간이 존재하지 않습니다.");
        }

        if(!sortedStations.contains(targetStation)){
            throw new IllegalArgumentException("노선에 존재하지 않는 역입니다.");
        }
    }

    private boolean isLastUpOrDownStation(List<Station> sortedStations, Station targetStation) {
        Station lastUpStation = sortedStations.get(0);
        Station lastDownStation = sortedStations.get(sortedStations.size()-1);

        return lastUpStation.equals(targetStation) || lastDownStation.equals(targetStation);
    }

    public List<Station> getSortedStations() {
        Map<Station, Section> upStationMap =
                sections.stream().collect(Collectors.toMap(key -> key.getUpStation(), val -> val));
        Map<Station, Section> downStationMap =
                sections.stream()
                        .collect(Collectors.toMap(key -> key.getDownStation(), val -> val));

        Station lastUpStation = new Station();

        for (Station station : upStationMap.keySet()) {
            if (downStationMap.getOrDefault(station, null) == null) {
                lastUpStation = station;
                break;
            }
        }

        List<Station> stations = new ArrayList<>();
        stations.add(lastUpStation);

        while (upStationMap.getOrDefault(lastUpStation, null) != null) {
            Section section = upStationMap.get(lastUpStation);
            stations.add(section.getDownStation());
            lastUpStation = section.getDownStation();
        }

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    private List<Station> getStations() {
        var upStations = sections.stream().map(Section::getUpStation);
        var downStations = sections.stream().map(Section::getDownStation);

        return Stream.concat(upStations, downStations).distinct().collect(Collectors.toList());


    }
    private boolean isNegativeDistance(int distance) {
        return distance <= 0;
    }
}