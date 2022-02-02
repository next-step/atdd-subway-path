package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    public Line(final String name, final String color) {
        validateName(name);
        validateColor(color);
        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        sections.addSection(section);
    }

    public void removeSection(final Station station) {
        sections.removeSection(station);
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
        validateName(name);
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        validateColor(color);
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    private void validateName(final String name) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("line name is not valid");
        }
    }

    private void validateColor(final String color) {
        if (Objects.isNull(color)) {
            throw new IllegalArgumentException("line color is not valid");
        }
    }
}
