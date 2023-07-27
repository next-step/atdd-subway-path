package nextstep.subway.line;


import nextstep.subway.exception.BadRequestException;
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

    public void addSection(LineSection lineSection) {
        if (sections.isMiddle(lineSection))
            sections.getSections().stream()
                    .filter(e -> e.getUpStation().equals(lineSection.getUpStation()))
                    .findFirst()
                    .ifPresent(e -> {
                        if(e.getDistance()<=lineSection.getDistance())
                            throw new BadRequestException("request section distance must be smaller than exist section disctance.");
                        sections.add(LineSection.of(this, e.getUpStation(), lineSection.getDownStation(), lineSection.getDistance()));
                        sections.add(LineSection.of(this, lineSection.getDownStation(), e.getDownStation(), e.getDistance()- lineSection.getDistance()));
                        sections.remove(e);
                    });
        else sections.add(lineSection);
    }


    public void removeSection(Station deleteStation) {
        this.sections.remove(deleteStation);
    }


    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
