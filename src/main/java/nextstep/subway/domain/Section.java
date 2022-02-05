package nextstep.subway.domain;

import lombok.Builder;
import nextstep.subway.domain.object.Distance;

import javax.persistence.CascadeType;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {
    }

    @Builder
    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public Distance getDistance() {
        return distance;
    }

    public Integer getDistanceValue() {
        return distance.getValue();
    }

    public void updateForAdd(Station downStation, Distance distance) {
        this.upStation = downStation;
        setDistance(distance);
    }

    public void updateForDelete(Station downStation, Distance distance) {
        this.downStation = downStation;
        setDistance(distance);
    }

    private void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Distance minusDistance(Distance distance) {
        return this.distance.minus(distance);
    }

    public Distance plusDistance(Distance distance) {
        return this.distance.plus(distance);
    }
}
