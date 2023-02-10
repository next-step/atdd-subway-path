package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        sections = new Sections();
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

    public void update(final String name, final String color) {
        setName(name);
        setColor(color);
    }

    public void setName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("노선 이름은 null일 수 없습니다.");
        }
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        if (color == null) {
            throw new IllegalArgumentException("노선 색깔은 null일 수 없습니다.");
        }
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(final Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(final Station station) {
        sections.remove(station);
    }
}
