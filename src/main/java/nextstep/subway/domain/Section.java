package nextstep.subway.domain;

import nextstep.subway.handler.validator.SectionValidator;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {

    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        SectionValidator.validateDistance(distance);
        return new Section(line, upStation, downStation, distance);
    }

    public boolean hasUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean hasStation(Station station) {
        return (this.upStation.equals(station) || this.downStation.equals(station));
    }

    @Override
    public String toString() {
        return "Section{" +
                "line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
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
}