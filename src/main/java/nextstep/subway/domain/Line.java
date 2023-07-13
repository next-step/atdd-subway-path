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
    LineSections lineSections = new LineSections(this);

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
        return lineSections.getStations();
    }

    public void removeSection() {
        lineSections.removeLastStation();
    }

    public void addSection(Section section) {
        lineSections.addSection(section);
    }

    public boolean isEmptySections() {
        return lineSections.isEmpty();
    }

    public int sizeSections() {
        return lineSections.size();
    }

    public Station getLastStation() {
        return lineSections.getDownStation();
    }

    public int getSectionsSize() {
        return lineSections.size();
    }
}
