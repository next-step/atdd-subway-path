package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private LineSections lineSections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.lineSections = new LineSections();
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
        return lineSections.getAllSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        lineSections.addSection(this, upStation, downStation, distance);
    }

    public List<Station> getOrderedStations() {
        return lineSections.getOrderedStations();
    }

    public void removeSection(Station downEndStation) {
        this.lineSections.removeSection(downEndStation);
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
