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

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
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

    public void update(Station newUpStation, int minusDistance) {
        this.upStation = newUpStation;
        this.distance = this.distance - minusDistance;
    }

    public Section merge(Section section) {
        if (!hasDownStationAs(section.upStation)) {
            throw new IllegalArgumentException("합치려는 구간의 상행역이 하행역과 같아야 합니다.");
        }

        return Section.of(line, upStation, section.downStation, distance + section.distance);
    }


    public boolean hasUpStationAs(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStationAs(Station station) {
        return downStation.equals(station);
    }

}
