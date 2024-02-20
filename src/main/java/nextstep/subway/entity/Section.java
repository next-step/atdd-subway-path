package nextstep.subway.entity;

import javax.persistence.*;

@Entity
public class Section implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "line_id",
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
        nullable = false
    )
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "down_station_id",
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
        nullable = false
    )
    private Station downStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "up_station_id",
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
        nullable = false
    )
    private Station upStation;

    @Column(nullable = false)
    private Integer distance;

    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateNextSection(Station upStation, Integer distance) {
        this.upStation = upStation;
        this.distance -= distance;
    }

    public void updatePrevSection(Station downStation, Integer distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    @Override
    public int compareTo(Section other) {
        return this.getDownStation().equals(other.getUpStation()) ? -1 : 1;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Integer getDistance() {
        return distance;
    }
}
