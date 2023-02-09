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
    private Sections sections = new Sections();

    protected Line() {
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

    public List<Section> sections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.stations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
    }

    public void removeSection(long stationId) {
        sections.remove(stationId);
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }
}
