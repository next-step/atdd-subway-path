package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;
    
    @Column(nullable = false)
    private Integer distance;

    @Embedded
    private Sections sections;

    public Line(Long id, String name, String color, Integer distance, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = Sections.init(new Section(this, upStation, downStation, distance));
    }

    public Line(String name, String color, Integer distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = Sections.init(new Section(this, upStation, downStation, distance));
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }
}
