package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        return sections.findAllStationsInOrder();
    }

    public void removeSection(final Station station) {
        sections.remove(station);
    }

    public void addSection(final Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public int getDistance() {
        return sections.getDistance();
    }
}
