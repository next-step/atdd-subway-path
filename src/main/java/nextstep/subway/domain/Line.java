package nextstep.subway.domain;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(this, upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public void updateLine(String name, String color) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }

        if (StringUtils.hasText(color)) {
            this.color = color;
        }
    }
}
