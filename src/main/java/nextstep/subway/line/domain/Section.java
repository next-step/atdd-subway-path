package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.common.domain.exception.ErrorMessage;
import nextstep.subway.station.domain.Station;

@Getter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "LINE_ID")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    @Builder
    public Section(Long id, Line line, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean matchId(long id) {
        return this.id.equals(id);
    }

    public boolean matchUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean matchDownStation(long id) {
        return downStation.matchId(id);
    }

    public void changeUpStation(Section newSection) {
        verifyDistanceExceeded(newSection.distance);

        distance = distance.subtraction(newSection.distance);
        upStation = newSection.getDownStation();
    }

    public void dockingInDownSection(Section oldSection, Section newSection) {
        newSection.distance = newSection.distance.addition(oldSection.distance);

        downStation = newSection.getDownStation();
        newSection.upStation = upStation;
    }

    private void verifyDistanceExceeded(Distance newSectionDistance) {
        if (newSectionDistance.greaterThan(this.distance) || newSectionDistance.equals(this.distance)) {
            throw new IllegalArgumentException(ErrorMessage.DISTANCE_EXCEEDED.getMessage());
        }
    }
}
