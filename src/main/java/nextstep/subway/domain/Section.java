package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Section {
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public boolean isMissMatchDownStation(Station station) {
        return !downStation.equals(station);
    }

    public boolean isConnectable(Section section) {
        return upStation.equals(section.getUpStation())
                || upStation.equals(section.getDownStation())
                || downStation.equals(section.getUpStation())
                || downStation.equals(section.getDownStation());
    }

    public void connectInside(Section section) {
        if (isConnectOutSide(section)) {
            throw new IllegalArgumentException();
        }
        distance.reduce(section.getDistance());
        changeEndPoint(section);
    }

    private boolean isConnectOutSide(Section section) {
        return !isConnectInSide(section);
    }

    public boolean isConnectInSide(Section section) {
        return isMatchUpStation(section) || isMatchDownStation(section);
    }

    private void changeEndPoint(Section section) {
        if (isMatchUpStation(section)) {
            upStation = section.getDownStation();
            return;
        }
        downStation = section.getUpStation();
    }

    private boolean isMatchUpStation(Section section) {
        return upStation.equals(section.getUpStation());
    }

    private boolean isMatchDownStation(Section section) {
        return downStation.equals(section.getDownStation());
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }
}