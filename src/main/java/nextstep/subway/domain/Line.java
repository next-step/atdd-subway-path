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
    Sections sections = new Sections(this);

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

    public void setColor(String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection() {
        sections.removeLastStation();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
    }

    public int sizeSections() {
        return sections.size();
    }

    public Station getLastStation() {
        return sections.getDownStation();
    }

    public int getSectionsSize() {
        return sections.size();
    }
}
