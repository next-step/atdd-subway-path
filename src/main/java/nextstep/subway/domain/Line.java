package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
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
        getSections().stream().filter(it -> it.getDownStation() == section.getDownStation()).findFirst()
                .ifPresent(it -> it.setPrevSection(section));
        getSections().stream().filter(it -> it.getUpStation() == section.getUpStation()).findFirst()
                .ifPresent(it -> it.setNextSection(section));
        getSections().add(section);
    }

    public List<Station> getStations() {
        List<Section> sections = getSections();
        Map<Station, List<Section>> map = sections.stream().collect(Collectors.groupingBy(Section::getUpStation));
        Section first = map.entrySet().stream()
                .filter(it -> it.getValue().size() == 1)
                .findFirst().orElseThrow()
                .getValue().get(0);
        return getStationsByRecursive(first, new ArrayList<>(Collections.singletonList(first.getUpStation())));
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

    public boolean isLastStation(Station station) {
        return getSections().get(getSections().size() - 1).getDownStation().equals(station);
    }

    public void removeSection() {
        getSections().remove(getSections().size() - 1);
    }
}
