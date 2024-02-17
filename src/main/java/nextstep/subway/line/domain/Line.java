package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE line SET deleted_at = CURRENT_TIMESTAMP where line_id = ?")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @Column
    private LocalDateTime deletedAt;

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void updateLine(String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public List<Section> getSections() {
        return this.sections.getSortedSections();
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
    }

    public void deleteSection(Station deletedStation) {
        sections.deleteSection(deletedStation);
    }

    public boolean hasSection(Section Section) {
        return sections.hasSection(Section);
    }

    public Long getLineId() {
        return lineId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(lineId, line.getLineId()) && Objects.equals(name, line.getName()) && Objects.equals(color, line.getColor()) && Objects.equals(getSections(), line.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, name, color, sections);
    }

}
