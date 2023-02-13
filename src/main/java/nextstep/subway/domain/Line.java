package nextstep.subway.domain;

import java.util.Arrays;
import java.util.Collection;
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

    public void removeSection(Station station) {
        if (!getSections().get(getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(getSections().size() - 1);
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(s -> Arrays.asList(s.getUpStation(), s.getDownStation()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
