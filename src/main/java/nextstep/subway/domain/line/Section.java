package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @OneToOne
    private Station upStation;

    @OneToOne
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isUp(Station station) {
        return upStation.equals(station);
    }

    public boolean isDown(Station station) {
        return downStation.equals(station);
    }

    public boolean isExistedDownStation(Section section) {
        return upStation.equals(section.getDownStation()) || downStation.equals(section.getDownStation());
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
