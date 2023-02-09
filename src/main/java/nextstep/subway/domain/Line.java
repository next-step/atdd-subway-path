package nextstep.subway.domain;

import lombok.Builder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            extendSection(upStation, downStation, distance);
            return;
        }

        checkStationStatus(upStation, downStation);

        Section oldSection = findSameUpStation(upStation);
        if (oldSection != null) {
            splitSection(downStation, distance, oldSection);
            return;
        }

        extendSection(upStation, downStation, distance);
    }

    private Section findSameUpStation(Station upStation) {
        return sections.stream().filter(s -> s.getUpStation().equals(upStation))
            .findFirst()
            .orElse(null);
    }

    private void splitSection(Station downStation, int distance, Section oldSection) {
        Station oldUpstation = oldSection.getUpStation();
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkDistance(distance, oldDistance);

        sections.remove(oldSection);
        sections.addAll(List.of(
            Section.builder()
                .line(this)
                .upStation(oldUpstation)
                .downStation(downStation)
                .distance(distance)
                .build(),
            Section.builder()
                .line(this)
                .upStation(downStation)
                .downStation(oldDownStation)
                .distance(oldDistance - distance)
                .build()
        ));
    }

    private void checkStationStatus(Station upStation, Station downStation) {
        List<Station> allStations = getAllStations();
        if (stationsAlreadyExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException(LineErrorMessage.STATIONS_ALREADY_EXIST.getMessage());
        }
        if (stationsNotExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException(LineErrorMessage.STATIONS_NOT_EXIST.getMessage());
        }
    }

    private boolean stationsNotExist(Station upStation, Station downStation, List<Station> allStations) {
        return !allStations.contains(upStation) && !allStations.contains(downStation);
    }

    private boolean stationsAlreadyExist(Station upStation, Station downStation, List<Station> allStations) {
        return allStations.contains(upStation) && allStations.contains(downStation);
    }

    private void checkDistance(int distance, int oldDistance) {
        if (distance >= oldDistance) throw new IllegalArgumentException(LineErrorMessage.INVALID_DISTANCE.getMessage());
    }

    public void extendSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        Section anySection = this.sections.stream().findFirst().orElse(null);

        if (anySection == null) {
            return stations;
        }

        stations.add(anySection.getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> stationMap = this.sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        List<Station> orderedStations = new ArrayList<>();

        EndStations endStations = findEndStations();
        Station upStation = endStations.upEndStation;
        Station downStation = stationMap.getOrDefault(upStation, null);

        orderedStations.add(upStation);

        while (upStation != endStations.downEndStation) {
            downStation = stationMap.getOrDefault(upStation, null);
            orderedStations.add(downStation);
            upStation = downStation;
        }

        return orderedStations;
    }

    private EndStations findEndStations() {
        List<Station> upStations = this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        Station upEndStation = upStations.stream()
            .filter(s -> !downStations.contains(s))
            .findFirst()
            .orElse(null);
        Station downEndStation = downStations.stream()
            .filter(s -> !upStations.contains(s))
            .findFirst()
            .orElse(null);

        return EndStations.builder()
            .upEndStation(upEndStation)
            .downEndStation(downEndStation)
            .build();
    }

    private static class EndStations {

        private Station upEndStation;
        private Station downEndStation;

        @Builder
        public EndStations(Station upEndStation, Station downEndStation) {
            this.upEndStation = upEndStation;
            this.downEndStation = downEndStation;
        }
    }

    public void removeSection(Station downEndStation) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(downEndStation)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

}
