package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.LineResponse;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }

    public LineResponse toLineResponse() {
        return new LineResponse(
                this.id,
                this.name,
                this.color,
                sections.createStationResponses()
        );
    }

    public void addSection(Section... section) {
        sections.add(section);
    }

    public Set<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void removeSection(Station station) {
        sections.removeLastSection(station);
    }

}
