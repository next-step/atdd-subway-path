package nextstep.subway.section.domain;

import nextstep.subway.exception.NotMatchUpStationAndDownStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
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
    private LocalDateTime deletedAt;

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

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public void updateDownStation(Station downStation) {
        if (this.upStation.equals(downStation)) {
            throw new NotMatchUpStationAndDownStationException();
        }
        this.downStation = downStation;
    }

    public void updateUpStation(Station upStation) {
        if (this.downStation.equals(upStation)) {
            throw new NotMatchUpStationAndDownStationException();
        }
        this.upStation = upStation;
    }

    public void delete() {
        deletedAt = LocalDateTime.now();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(sectionId, section.getSectionId()) && Objects.equals(line, section.getLine()) && Objects.equals(upStation, section.getUpStation()) && Objects.equals(downStation, section.getDownStation()) && Objects.equals(distance, section.getDistance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId, line, upStation, downStation, distance, deletedAt);
    }

}
