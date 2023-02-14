package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
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
        return sections.getSections();
    }

    public void addSection(Section newSection) {
        this.sections.add(this, newSection);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public int getLineDistance() {
        return sections.getSectionDistances()
                .stream()
                .reduce(0, Integer::sum);
    }

    public List<Integer> getSectionDistances() {
        return sections.getSectionDistances();
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
