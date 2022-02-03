package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
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

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getAllSections();
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (this.color != null) {
            this.color = color;
        }
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getAllStations() {
        return sections.getAllStations();
    }

    public void removeSection(Station lastDownStation) {
        sections.removeSection(lastDownStation);
    }
}
