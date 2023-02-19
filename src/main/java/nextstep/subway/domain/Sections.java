package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    private static final String NEGATIVE_DISTANCE = "구간의 거리는 0보다 커야합니다.";
    private static final String EXIST_STATION = "추가하려는 구간의 상/하행역이 이미 노선에 포함되어 있습니다.";
    private static final String NON_EXIST_UP_DOWN_STATION = "추가하려는 구간의 상/하행역이 노선에 포함되어 있지 않습니다.";
    private static final String NO_SECTION = "노선에 구간이 존재하지 않습니다.";
    private static final String NON_EXIST_STATION = "노선에 존재하지 않는 역입니다.";

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if(sections.isEmpty()){
            sections.add(newSection);
            return;
        }

        validateCanAdd(newSection);

        if(canAddFirstOrLst(newSection)){
            sections.add(newSection);
            return;
        }

        addToMiddle(newSection);
    }

    private boolean canAddFirstOrLst(Section newSection) {
        return getLastUpStation().get().equals(newSection.getDownStation()) || getLastDownStation().get().equals(newSection.getUpStation());
    }

    private void addToMiddle(Section newSection) {
        Section targetSection = findByStation(newSection);

        if (targetSection.isLessThan(newSection)) {
            throw new IllegalArgumentException("추가된 구간이 기존 구간보다 길이가 깁니다.");
        }

        sections.add(newSection);
        targetSection.divideSection(newSection);
    }

    private Section findByStation(Section newSection){
        return sections.stream()
                        .filter(e -> e.equalsUpOrDownStation(newSection))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
    }

    private void validateCanAdd(Section newSection) {
        if (isNegativeDistance(newSection.getDistance())) {
            throw new IllegalArgumentException(NEGATIVE_DISTANCE);
        }

        if (isAllStationExist(newSection)) {
            throw new IllegalArgumentException(EXIST_STATION);
        }

        if (isAllStationNotExist(newSection)) {
            throw new IllegalArgumentException(NON_EXIST_UP_DOWN_STATION);
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

    public void delete(Station station) {
        validateCanDelete(station);

        if(isFirstOrLastStation(station)){
            deleteFirstOrLast(station);
            return;
        }

        deleteMiddle(station);
    }

    private void deleteFirstOrLast(Station targetStation) {
        Optional<Section> targetSection = getSectionByUpStation(targetStation);

        if(targetSection.isEmpty()){
            targetSection = getSectionByDownStation(targetStation);
        }

        sections.remove(targetSection.get());
    }

    private void deleteMiddle(Station targetStation) {
        Section targetUpSection = getSectionByUpStation(targetStation).orElseThrow(IllegalArgumentException::new);
        Section targetDownSection = getSectionByDownStation(targetStation).orElseThrow(IllegalArgumentException::new);

        targetUpSection.addDistance(targetDownSection.getDistance());
        targetUpSection.updateDownStation(targetDownSection.getDownStation());

        sections.remove(targetDownSection);
    }

    private void validateCanDelete(Station targetStation){
        if(sections.isEmpty()){
            throw new IllegalArgumentException(NO_SECTION);
        }

        if(!getStations().contains(targetStation)){
            throw new IllegalArgumentException(NON_EXIST_STATION);
        }
    }

    private boolean isFirstOrLastStation(Station targetStation) {
        Optional<Station> lastUpStation = getLastUpStation();
        Optional<Station> lastDownStation = getLastDownStation();

        return lastUpStation.get().equals(targetStation) || lastDownStation.get().equals(targetStation);
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

    private Optional<Station> getLastUpStation(){
        if (sections.size() == 0){
            return Optional.empty();
        }

        List<Station> sortedStations = getSortedStations();
        return Optional.of(sortedStations.get(0));
    }

    private Optional<Station> getLastDownStation(){
        if (sections.size() == 0){
            return Optional.empty();
        }

        List<Station> sortedStations = getSortedStations();
        return Optional.of(sortedStations.get(sortedStations.size()-1));
    }

    private Optional<Section> getSectionByUpStation(Station station){
        return sections.stream()
                    .filter(e -> e.equalsDownStation(station))
                    .findFirst();
    }

    private Optional<Section> getSectionByDownStation(Station station){
        return sections.stream()
                    .filter(e -> e.equalsUpStation(station))
                    .findFirst();
    }
}