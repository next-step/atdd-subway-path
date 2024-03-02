package nextstep.subway.domain.entity;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String color;
    @Embedded
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void addSection(Section section) {
        section.setLine(this);
        this.sections.addSection(section);
    }

    public void removeSection(Station station) {
        this.sections.removeStation(station);
    }


    public List<Station> getAllStations() {
        return this.sections.getSortedStationsByUpDirection(true);
    }

    public List<Station> getAllStationsByDistinct() {
        return this.sections.getSortedStationsByUpDirection(true)
            .stream().distinct()
            .collect(Collectors.toList());
    }


}
