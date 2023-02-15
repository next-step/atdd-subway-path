package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.domain.validator.SectionValidator.*;
import static nextstep.subway.domain.validator.SectionValidator.checkDistance;



@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

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

    public List<Section> getSections() {
        return sections.getAllSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(this, upStation, downStation, distance);
    }

    public void removeSection(Station station) {
        sections.remove(this,station);

    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public List<Station> getStations() {
        return sections.getStations();
    }
}
