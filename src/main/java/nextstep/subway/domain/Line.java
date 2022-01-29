package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();
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

    public Sections getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.addSection(section);
        section.updateLine(this);
    }

    public boolean isNotDownStation(Long station) {
        for (Section section : sections.getSections()) {
            if (section.isSameDownStation(station)) {
                return false;
            }
        }
        return true;
    }

    public void deleteSection(Long stationId) {

        sections.deleteSection(stationId);
    }

}
