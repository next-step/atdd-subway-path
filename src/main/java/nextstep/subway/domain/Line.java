package nextstep.subway.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public Line(String name, String color) {
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

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getStations() {
        final Set<Station> stationSet = new HashSet<>();
        sections.forEach(section -> {
            stationSet.add(section.getUpStation());
            stationSet.add(section.getDownStation());
        });

        return List.copyOf(stationSet);
    }

    public void removeSection(Station upStation, Station downStation) {
        final var isSameStation = generateSameStationChecker(upStation, downStation);

        sections = sections.stream()
            .filter(isSameStation)
            .collect(Collectors.toList());
    }

    private static Predicate<Section> generateSameStationChecker(Station upStation, Station downStation) {
        return section ->
            section.getUpStation() != upStation
                && section.getDownStation() != downStation;
    }
}
