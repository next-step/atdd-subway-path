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

    @Embedded
    private LineSection lineSection;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.lineSection = new LineSection();
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
        return lineSection.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        lineSection.addSection(this, upStation, downStation, distance);
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> stationMap = getSections().stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        List<Station> orderedStations = new ArrayList<>();

        EndStations endStations = findEndStations();
        Station upStation = endStations.upEndStation;
        Station downStation;

        orderedStations.add(upStation);

        while (upStation != endStations.downEndStation) {
            downStation = stationMap.getOrDefault(upStation, null);
            orderedStations.add(downStation);
            upStation = downStation;
        }

        return orderedStations;
    }

    private EndStations findEndStations() {
        List<Station> upStations = getSections().stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Station> downStations = getSections().stream()
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
        this.lineSection.removeSection(downEndStation);
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
