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

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean isEnoughDistance(int distance) {
        return this.distance > distance;
    }

    public boolean isAddBetween(Section section) {
        return this.upStation.equals(section.upStation) || this.downStation.equals(section.downStation);
    }

    public void changeUpSection(Station newDownStation, int distance) {
        this.downStation = newDownStation;
        this.distance = minusDistance(distance);
    }

    public void changeDownSection(Station newUpStation, int distance) {
        this.upStation = newUpStation;
        this.distance = minusDistance(distance);
    }

    public int minusDistance(int distance) {
        return this.distance - distance;
    }

    public boolean isSameAsUpStation(Station station) {
        boolean equals = this.upStation.equals(station);
        return equals;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }

    public boolean isSameStations(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }
}