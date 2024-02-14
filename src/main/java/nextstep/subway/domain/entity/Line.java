package nextstep.subway.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Column(nullable = false)
    private int distance;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> getSectionList() {
        return this.sections.getSections();
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public void addSection(Section section) {
        sections.addSection(section);
        this.distance = sections.getDistance();
    }

    public void deleteSection(Section section) {
        sections.deleteSection(section);
        this.distance -= section.getDistance();
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void deleteStation(Station station) {
        sections.deleteStation(station);
        this.distance = sections.getDistance();
    }
}
