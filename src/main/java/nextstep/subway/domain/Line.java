package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }


    public void addSection(final Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Station getTerminalStation() {
        return sections.getTerminalStation();
    }

    public void removeStation(final Station station) {
        sections.removeSection(station);
    }

    public boolean hasEmptySection() {
        return sections.isEmpty();
    }
}
