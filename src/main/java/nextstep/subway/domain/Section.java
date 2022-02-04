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

    protected Section() {
    }

    public Section(Line line, Station downStation, Station upStation, int distance) {
        this.line = line;
        this.downStation = downStation;
        this.upStation = upStation;
        this.distance = distance;
    }
    public Section(Station upStation, Station downStation, int distance) {
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

    public boolean isEqualDownStationName(String name) {
        return getDownStation().isEqualName(name);
    }

    public boolean isEqualUpStationName(String name) {
        return getUpStation().isEqualName(name);
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
}