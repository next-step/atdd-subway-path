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

    public void initSection(Section newSection) {
        sections.add(newSection);
    }

    public void addSection(Section newSection) {
        if(sections.isEmpty()){
            sections.add(newSection);
            return;
        }

        if (isAllStationExist(newSection.getUpStation(), newSection.getDownStation())) {
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 이미 노선에 포함되어 있습니다.");
        }

        if (isAllStationNotExist(newSection.getUpStation(), newSection.getDownStation())) {
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 노선에 포함되어 있지 않습니다.");
        }

        Map<Station, Integer> upStationMap = getUpStationMap();
        Map<Station, Integer> downStationMap = getDownStationMap();

        Station lastUpStation = new Station();
        Station lastDownStation = new Station();

        for (Station station : downStationMap.keySet()) {
            Integer value = upStationMap.getOrDefault(station, 0);

            if (value != 0) upStationMap.put(station, value + 1);
            else lastDownStation = station;
        }

        for (Station station : upStationMap.keySet()) {
            if (upStationMap.getOrDefault(station, 0) == 1) {
                lastUpStation = station;
            }
        }

        if (newSection.getUpStation().equals(lastDownStation)) {
            sections.add(newSection);
            return;
        }

        if (newSection.getDownStation().equals(lastUpStation)) {
            sections.add(0, newSection);
            return;
        }

        if (!newSection.getUpStation().equals(lastDownStation)
                && !newSection.getDownStation().equals(lastUpStation)) {
            Integer sectionIndex = getSectionIndexByUpStation(newSection.getUpStation());

            if (sectionIndex == -1) {
                sectionIndex = getSectionIndexByDownStation(newSection.getDownStation());

                Section targetSection = sections.get(sectionIndex);

                if (targetSection.getDistance() <= newSection.getDistance()) {
                    throw new IllegalArgumentException("추가된 구간이 기존 구간보다 길이가 깁니다.");
                }

                targetSection.updateDistance(
                        targetSection.getDistance() - newSection.getDistance());
                targetSection.updateDownStation(newSection.getUpStation());
                sections.set(sectionIndex, targetSection);
                sections.add(sectionIndex - 1, newSection);
                return;
            }

            Section targetSection = sections.get(sectionIndex);

            if (targetSection.getDistance() <= newSection.getDistance()) {
                throw new IllegalArgumentException("추가된 구간이 기존 구간보다 길이가 깁니다.");
            }

            targetSection.updateDistance(targetSection.getDistance() - newSection.getDistance());
            targetSection.updateUpStation(newSection.getDownStation());
            sections.set(sectionIndex, targetSection);
            sections.add(sectionIndex, newSection);
        }
    }

    public void deleteSection(Long stationId) {
        if(!isLastDownStation(stationId)){
            throw new IllegalArgumentException("하행종점역인 경우만 삭제가 가능합니다.");
        }

        for(int i = 0; i < sections.size(); i++){
            if(sections.get(i).getDownStation().getId() == stationId){
                sections.remove(i);
                return;
            }
        }
    }

    private boolean isLastDownStation(Long stationId) {
        List<Station> stations = getSortedStations();
        Station lastStation = stations.get(stations.size()-1);

        if(lastStation.getId() == stationId){
            return true;
        }

        return false;
    }

    public Integer getSectionSize() {
        return sections.size();
    }

    public List<Section> getSections() {
        return sections;
    }

    private List<Station> getStations() {
        var upStations = sections.stream().map(Section::getUpStation);
        var downStations = sections.stream().map(Section::getDownStation);

        return Stream.concat(upStations, downStations).distinct().collect(Collectors.toList());
    }

    private boolean isAllStationExist(Station upStation, Station downStation) {
        List<Station> stations = getStations();

        if (stations.contains(upStation) && stations.contains(downStation)) {
            return true;
        }

        return false;
    }

    private boolean isAllStationNotExist(Station upStation, Station downStation) {
        List<Station> stations = getStations();

        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            return true;
        }

        return false;
    }

    private Map<Station, Integer> getUpStationMap() {
        return sections.stream().collect(Collectors.toMap(key -> key.getUpStation(), val -> 1));
    }

    private Map<Station, Integer> getDownStationMap() {
        return sections.stream().collect(Collectors.toMap(key -> key.getDownStation(), val -> 1));
    }

    private Integer getSectionIndexByUpStation(Station upStation) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(upStation)) {
                return i;
            }
        }

        return -1;
    }

    private Integer getSectionIndexByDownStation(Station downStation) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getDownStation().equals(downStation)) {
                return i;
            }
        }

        return -1;
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
}
