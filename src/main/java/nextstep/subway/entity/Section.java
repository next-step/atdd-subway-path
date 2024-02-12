package nextstep.subway.entity;

import javax.persistence.*;

@Entity
public class Section {
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

    public Section(Line line, Station downStation, Station upStation, Integer distance) {
        this.line = line;
        this.downStation = downStation;
        this.upStation = upStation;
        this.distance = distance;
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
