package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Sections sections = new Sections();

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
        this.name = name;
        this.color = color;
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

    public void removeSectionByStationId(Long stationId) {
        sections.removeByStationId(stationId);
    }

    public Section getLastSection() {
        return sections.last();
    }

    public int getSectionsSize() {
        return sections.size();
    }
}
