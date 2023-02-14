package nextstep.subway.domain;

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

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        add(upStation, downStation, distance);
    }

    private void add(Station upStation, Station downStation, int distance) {
        sections.add(this, upStation, downStation, distance);
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

    public Sections getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        add(upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection() {
        sections.removeSection();
    }

    public Section getFirstSection() {
        return sections.getFirstSection();
    }

    public Section getLastSection() { return sections.getLastSection(); }

    public Station getDownStation() {
        return sections.getDownStation();
    }
}
