package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.exception.line.InvalidDistanceException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(cascade = PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
    public void update(final Section newSection) {
        updateToNewStation(newSection);
        updateDistance(newSection.getDistance());
    }

    private void updateToNewStation(Section newSection) {
        if (upStation.equals(newSection.getUpStation())) {
            this.upStation = newSection.getDownStation();
        } else if(downStation.equals(newSection.getDownStation())) {
            this.downStation = newSection.getUpStation();
        } else {
            throw new AssertionError("validate 실패");
        }
    }

    private void updateDistance(final int distance) {
        if (this.distance <= distance) {
            throw new InvalidDistanceException();
        }
        this.distance -= distance;
    }
}