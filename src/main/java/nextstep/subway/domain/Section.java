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

    @Embedded
    private Distance distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section changeUpStation(Section otherSection) {
        return new Section(line, otherSection.downStation, downStation, distance.minus(otherSection.distance));
    }

    public Section changeDownStation(Section otherSection) {
        return new Section(line, upStation, otherSection.upStation, distance.minus(otherSection.distance));
    }

    public Section combine(Section downSection) {
        return new Section(line, upStation, downSection.downStation, distance.plus(downSection.distance));
    }

    public boolean equalsUpAndDownStation(Section section) {
        return hasSameUpStation(section) && hasSameDownStation(section);
    }

    public boolean hasSameUpStation(Section otherSection) {
        return upStation.equals(otherSection.upStation);
    }

    public boolean hasSameDownStation(Section otherSection) {
        return downStation.equals(otherSection.downStation);
    }

    public boolean isPreviousSection(Section otherSection) {
        return downStation.equals(otherSection.upStation);
    }

    public boolean isNextSection(Section otherSection) {
        return upStation.equals(otherSection.downStation);
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
        return distance.getDistance();
    }
}
