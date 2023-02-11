package nextstep.subway.domain.line;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Name;
import nextstep.subway.domain.station.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Name name;
    private String color;
    private Sections sections;

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = color;
        this.sections = new Sections();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(LineRequest lineRequest) {
        this.name = new Name(lineRequest.getName());
        this.color = lineRequest.getColor();
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public String getName() {
        if (name == null) {
            return null;
        }
        return name.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
