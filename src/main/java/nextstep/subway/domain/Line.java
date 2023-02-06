package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
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
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .map(s -> List.of(s.getUpStation(), s.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeSection(Station downEndStation) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(downEndStation)) {
            throw new IllegalArgumentException();
        }
        this.getSections().remove(this.getSections().size() - 1);
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
