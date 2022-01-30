package nextstep.subway.domain;


import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        Line result = new Line(name, color);
        result.sections.addFirstSection(Section.of(result, upStation, downStation, distance));

        return result;
    }

    public void updateInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = Section.of(this, upStation, downStation, distance);
        sections.addSection(section);
    }

    public void deleteStation(Station deleteStation) {
        sections.deleteStation(deleteStation);
    }

    public List<Station> getStations() {
        return sections.getAllStations();
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

}
