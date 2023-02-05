package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.domain.sections.Sections;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections(this);

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public void deleteSection(Long stationId) {
        sections.deleteSection(stationId);
    }

    public boolean canAddSection(Long upStationId, Long downStationId, int distance) {
        return upStationId != null && downStationId != null && distance != 0;
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

    public List<Section> getSections() {
        return sections.getValue();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Line line = (Line)o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }
}
