package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.applicaion.dto.LineRequest;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private final Sections sections = new Sections();

    private String name;
    private String color;

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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(LineRequest lineRequest) {
        if (lineRequest.getName() != null) {
            this.name = lineRequest.getName();
        }
        if (lineRequest.getColor() != null) {
            this.color = lineRequest.getColor();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id)
            && Objects.equals(name, line.name)
            && Objects.equals(color, line.color)
            && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }
}
