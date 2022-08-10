package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public void addSection(Section section) {
        List<Section> sections = getSections();

        if (sections.size() > 0) {
            checkOneStationExistsInLine(section);

            sections.stream().filter(it -> isPrevSection(it, section)).findFirst()
                    .ifPresent(it -> {
                                it.checkDistanceDividable(section);
                                it.setPrevSection(section);
                            }
                    );
            sections.stream().filter(it -> isNextSection(it, section)).findFirst()
                    .ifPresent(it -> {
                        it.checkDistanceDividable(section);
                        it.setNextSection(section);
                    });
        }

        sections.add(section);
    }

    private boolean isPrevSection(Section it, Section target) {
        return it.getDownStation() == target.getDownStation();
    }

    private boolean isNextSection(Section it, Section target) {
        return it.getUpStation() == target.getUpStation();
    }

    private void checkOneStationExistsInLine(Section section) {
        List<Station> stations = getStations().stream().filter(it -> it == section.getUpStation() || it == section.getDownStation()).collect(Collectors.toList());
        if (stations.size() != 1) {
            throw new IllegalArgumentException();
        }
    }

    public List<Station> getStations() {
        Section firstSection = getFirstSection(getSections());
        return getStationsByRecursive(firstSection, new ArrayList<>(Collections.singletonList(firstSection.getUpStation())));
    }

    private Section getFirstSection(List<Section> sections) {
        List<Station> upStations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        upStations.removeAll(downStations);

        Station firstStation = upStations.get(0);
        return sections.stream().filter(it -> it.getUpStation() == firstStation).findFirst().get();
    }

    private Section getNextSection(Section section) {
        return sections.stream().filter(it -> section.getDownStation() == it.getUpStation()).findFirst().orElse(null);
    }

    private List<Station> getStationsByRecursive(Section target, List<Station> res) {
        if (target == null) {
            return res;
        }
        res.add(target.getDownStation());
        Section nextSection = getNextSection(target);
        return getStationsByRecursive(nextSection, res);
    }

    public void removeStation(Station station) {
        Section target = getSections().stream().filter(it -> it.getUpStation() == station).findFirst().orElse(null);
        Section prev = getSections().stream().filter(it -> it.getDownStation() == station).findFirst().orElse(null);

        if (isLastStation(target)) {
            sections.remove(prev);
            return;
        }

        sections.remove(target);
        prev.setDownStation(target.getDownStation());
    }

    private boolean isLastStation(Section target) {
        return target == null;
    }

    public void checkHasMoreThanOneSection() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException();
        }
    }
}
