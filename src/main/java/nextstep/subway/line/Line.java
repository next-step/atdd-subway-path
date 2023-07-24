package nextstep.subway.line;


import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;
    @Embedded
    private LineSections sections;

    protected Line() {
    }
    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        Line line = new Line();
        line.name = name;
        line.color = color;
        line.sections = LineSections.of(line, upStation, downStation, distance);
        return line;
    }
    public void update(String name, String color) {
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

    public LineSections getSections() {
        return sections;
    }

    public void addSection(LineSection section) {
        this.sections.add(section);
    }

    public void removeSection(Station toDeleteStation) {
        this.sections.remove(toDeleteStation);
    }


    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
