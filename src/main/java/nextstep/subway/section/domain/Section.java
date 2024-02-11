package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE section SET deleted_at = CURRENT_TIMESTAMP where section_id = ?")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Column
    private Integer distance;

    @Column
    private Timestamp deleted_at;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Integer distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean equalsLastStation(Station station) {
        return this.downStation.equals(station);
    }

    public void delete() {
        deleted_at = Timestamp.valueOf(LocalDateTime.now());
    }

    public Long getSectionId() {
        return this.sectionId;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Timestamp getDeleted_at() {
        return deleted_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Section section = (Section) o;
        return Objects.equals(sectionId, section.getSectionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

}
