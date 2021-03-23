package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section implements Comparable<Section>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {}

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

    public boolean isSameUpStation(Section section) {
        return this.getUpStation().equals(section.getUpStation());
    }

    public boolean addable(Section section) {
        return this.distance > section.getDistance();
    }

    public void extendSection(Station downStation, int distance) {
        this.downStation = downStation;
        this.distance += distance;
    }

    @Override
    public int compareTo(Section section) {
        if (getDownStation().equals(section.getUpStation())) return -1;
        if (getUpStation().equals(section.getDownStation())) return 1;
        return 0;
    }
}
