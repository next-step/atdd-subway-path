package nextstep.subway.domain.line;

import lombok.Getter;
import nextstep.subway.domain.line.sections.Sections;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void change(String name, String color){
        if (!"".equals(name)) {
            this.name = name;
        }
        if (!"".equals(color)) {
            this.color = color;
        }
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
