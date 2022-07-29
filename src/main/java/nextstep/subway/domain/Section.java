package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.InvalidDistanceValueException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"id"})
public class Section {

    private static final int ZERO = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne
    private Station upStation;

    @OneToOne
    private Station downStation;

    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean containsStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public void updateUpStation(Section section) {
        this.upStation = section.downStation;
        this.distance = this.distance - section.distance;
    }

    public void updateDownStation(Section section) {
        downStation = section.upStation;
        this.distance = this.distance - section.distance;
    }

    public boolean isDistanceGreaterThan(Section section) {
        return this.distance < section.distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    private void validateDistance(int distance) {
        if (distance < ZERO) {
            throw new InvalidDistanceValueException(distance);
        }
    }

}
