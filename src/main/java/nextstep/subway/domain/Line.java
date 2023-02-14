package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public void update(String name, String color) {

        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(color)) {
            this.color = color;
        }
    }

    public void addSection(Section section) {
        if (sections.contains(section)) {
            return;
        }

        sections.add(section);
        section.addLine(this);
    }

    public int getSectionsSize() {
        return sections.size();
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }
}
