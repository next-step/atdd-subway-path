package nextstep.subway.line.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

@Entity(name = "lines")
@Getter
@NoArgsConstructor
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private LineSections lineSections;

    public Line(String name, String color, LineSections lineSections) {
        this.name = name;
        this.color = color;
        this.lineSections = lineSections;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(LineSection lineSection) {
        lineSections.addSection(lineSection);
    }

    public void deleteSection(Station station) {
        lineSections.deleteSection(station);
    }

}
