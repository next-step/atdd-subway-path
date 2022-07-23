package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(this, upStation, downStation, distance);
    }

    public Station upStation() {
        return this.sections.upStation();
    }

    public Station downStation() {
        return this.sections.downStation();
    }

    public List<Section> sections() {
        return this.sections.sections();
    }

    public void deleteSection(Station station) {
        this.sections.deleteSection(station);
    }
    public Station upStation(Station station) {
        return this.sections.upStation();
    }
    public Station downStation(Station station) {
        return this.sections.downStation();
    }

    public List<Station> stations() {
        return sections.stations();
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
