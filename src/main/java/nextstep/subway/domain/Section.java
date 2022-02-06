package nextstep.subway.domain;

import javax.persistence.*;

@Entity
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

    private int distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean isDuplicateStation(Station upStation, Station downStation) {
        return (this.upStation.isEqualName(upStation) && this.downStation.isEqualName(downStation))
            || (this.upStation.isEqualName(downStation) && this.downStation.isEqualName(upStation));
    }

    public boolean isEqualUpStation(Station upStation) {
        return this.upStation.isEqualName(upStation);
    }

    public boolean isEqualDownStation(Station downStation) {
        return this.downStation.isEqualName(downStation);
    }

    public boolean isGraterOrEqualThanDistance(int distance) {
        return this.distance >= distance;
    }
}