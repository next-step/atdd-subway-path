package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.springframework.data.geo.Distance;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if(sections.isEmpty()) {
            return List.of();
        }

        List<Station> stations = new ArrayList<>();

        Station firstStation = getFirstStation();
        stations.add(firstStation);

        Optional<Station> downStation = nextSectionDownStation(firstStation);
        while (downStation.isPresent()) {
            downStation.ifPresent(station -> stations.add(station));
            downStation = nextSectionDownStation(downStation.get());
        }

        return stations;
    }

    public void addSection(Section section) {
        if (this.sections.isEmpty()) {
            sections.add(section);
            section.setLine(this);
            return;
        }

        //중복된 역
        validationAddSection(section);

        //첫, 마지막 정류장일때
        if (getFirstStation().getId().equals(section.getDownStation())
                || getLastStation().getId().equals(section.getUpStation())) {
            sections.add(section);
            section.setLine(this);
            return;
        }

        //상행에 추가할 때
        if(isAddUpSection(section.getUpStation())) {
            Section upSection = sections.stream().filter(s -> s.getUpStation().getId().equals(section.getUpStation().getId()))
                                        .findFirst().get();
            upSection.setUpStation(section.getDownStation());
            calcDistanceByAddSection(upSection, section.getDistance());
        }

        //하행에 추가할 때
        if(isAddDownSection(section.getDownStation())) {
            Section downSection = sections.stream().filter(s -> s.getDownStation().getId().equals(section.getDownStation().getId()))
                                        .findFirst().get();
            downSection.setDownStation(section.getUpStation());
            calcDistanceByAddSection(downSection, section.getDistance());
        }

        sections.add(section);
        section.setLine(this);
    }



    private void validationAddSection(Section section) {
        //두 역이 모두 등록되어 있으면 안된다.
        if (getStations().stream()
                .anyMatch(station -> getUpStations().contains(section.getUpStation()) && getDownStations().contains(section.getDownStation()))) {
            throw new CustomException(CustomException.DUPLICATE_STATION_MSG);
        }

        //두 역이 중 한 역은 기존 구간에 포함되어 있어야한다.
        if(!isAddUpSection(section.getUpStation()) &&
                !isAddDownSection(section.getDownStation())) {
            throw new CustomException(CustomException.ADD_STATION_MUST_INCLUDE_IN_LINE);
        }
    }

    public void removeSection(Station station) {
        if(sections.isEmpty()) {
            throw new CustomException(CustomException.NO_SECTION_IN_LINE_MSG);
        }

        Section lastSection = sections.get(sections.size() - 1);
        if(!station.getId().equals(lastSection.getDownStation().getId())) {
            throw new CustomException(CustomException.ONLY_CAN_REMOVE_LAST_STATION_MSG);
        }

        sections.remove(sections.size() - 1);
    }

    private List<Station> getUpStations() {
        return this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Station getFirstStation() {
        Station firstStation = getUpStations().stream()
                .filter(station -> !getDownStations().contains(station))
                .findFirst().orElseThrow(() -> new CustomException(CustomException.INVALID_STATIONS_IN_LINE_MSG));
        return firstStation;
    }

    private Station getLastStation() {
        Station lastStation = getDownStations().stream()
                .filter(station -> !getUpStations().contains(station))
                .findFirst().orElseThrow(() -> new CustomException(CustomException.INVALID_STATIONS_IN_LINE_MSG));
        return lastStation;
    }

    private Optional<Station> nextSectionDownStation(Station station) {
        Optional<Section> nextSection = sections.stream()
                .filter(section -> section.getUpStation().getId().equals(station.getId()))
                .findFirst();

        if(!nextSection.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(nextSection.get().getDownStation());
    }

    private boolean isAddUpSection(Station station) {
        return sections.stream().filter(s -> s.getUpStation().getId().equals(station.getId()))
                .findFirst()
                .isPresent();
    }

    private boolean isAddDownSection(Station station) {
        return sections.stream().filter(s -> s.getDownStation().getId().equals(station.getId()))
                .findFirst()
                .isPresent();
    }

    private void calcDistanceByAddSection(Section section, int distance) {
        if(section.getDistance() <= distance) {
            throw new CustomException(CustomException.CAN_NOT_ADD_SECTION_CAUSE_DISTANCE);
        }
        section.minusDistance(distance);
    }
}
