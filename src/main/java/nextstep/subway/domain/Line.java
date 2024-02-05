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
        final Section sectionToAdd = new Section(this, upStation, downStation, distance);
        validateSectionToAdd(sectionToAdd);

        final Predicate<Section> isTheSectionToSplit =
            section ->
                section.getUpStation().equals(upStation)
                || section.getDownStation().equals(downStation);

        sections.stream()
            .filter(isTheSectionToSplit)
            .findAny()
            .ifPresent(sectionToSplit -> sectionToSplit.splitBySection(sectionToAdd));

        sections.add(sectionToAdd);
    }

    private void validateSectionToAdd(Section sectionToAdd) {
        final Set<Station> stationSet = new HashSet<>(getStations());

        if(stationSet.containsAll(List.of(sectionToAdd.getUpStation(), sectionToAdd.getDownStation()))) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if(!stationSet.contains(sectionToAdd.getUpStation()) && !stationSet.contains(sectionToAdd.getDownStation())) {
            throw new IllegalArgumentException("연결할 수 없는 구간입니다.");
        }
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
        final Predicate<Section> isNotTheSectionToDelete =
            section ->
                section.getUpStation() != upStation
                && section.getDownStation() != downStation;

        sections = sections.stream()
            .filter(isNotTheSectionToDelete)
            .collect(Collectors.toList());
    }
}
