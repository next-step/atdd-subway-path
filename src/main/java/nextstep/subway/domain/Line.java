package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.DuplicationException;

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

    public void initSection(Section section) {
        sections.addSection(section);
        section.updateLine(this);
    }

    public void addSection(Section section) {
        duplicationSection(section);
        sections.addSection(section);
        section.updateLine(this);
    }

    private void duplicationSection(Section section) {
        if (sections.contains(section)) {
            throw new DuplicationException();
        }
    }

    private boolean isNotDownStation(Station station) {
        return sections.isNotDownStation(station.getId());
    }

    public void deleteSection(Long stationId) {

        sections.deleteSection(stationId);
    }

    public int getSectionSize() {
        return sections.getSize();
    }

}
