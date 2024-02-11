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
    private Sections sections = new Sections();;

    @Column
    private Integer distance;

    @Column
    private LocalDateTime deleted_at;

    protected Line() {
    }

    private Line(String name, String color, Integer distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public static Line from(String name, String color, Integer distance) {
        return new Line(name, color, distance);
    }

    public void updateLine(String color, Integer distance) {
        this.color = color;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
    }

    public void deleteSection(Station deletedStation) {
        sections.deleteSection(deletedStation);
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

    public Integer getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Line line = (Line) o;
        return Objects.equals(lineId, line.getLineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId);
    }

}
