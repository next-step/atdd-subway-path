package nextstep.subway.line.section;


import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "lineId")
    private Line line;
    @ManyToOne
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStationId")
    private Station downStation;

    @Column
    private Long distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> stations() {
        return List.of(upStation, downStation);
    }

    public boolean matchStations(Section section) {
        return upStation.equals(section.getUpStation()) && downStation.equals(section.getDownStation());
    }

    public void moveBackFrom(Section prevSection) {
        this.upStation = prevSection.downStation;
        this.distance = this.distance - prevSection.distance;
    }

    public void mergeWith(Section nextSection) {
        this.downStation = nextSection.downStation;
        this.distance = distance + nextSection.distance;
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

    public Long getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
