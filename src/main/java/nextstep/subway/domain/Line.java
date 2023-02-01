package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
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

    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    @Deprecated
    public void setColor(String color) {
        this.color = color;
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
        section.changeLine(this);
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public Section getLastSection() {
        return sections.getLast();
    }

    public int getSectionsSize() {
        return sections.size();
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }
}
