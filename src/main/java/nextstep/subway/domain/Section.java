package nextstep.subway.domain;

import nextstep.subway.common.exception.section.SectionDistanceException;

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

    private Integer distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
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

    public void splitSection(Section newSection) {
        if (this.distance <= newSection.getDistance()) {
            throw new SectionDistanceException();
        }

        if (this.upStation.equals(newSection.upStation)) {
            this.upStation = newSection.getDownStation();
        }
        if (this.downStation.equals(newSection.downStation)) {
            this.downStation = newSection.getUpStation();
        }

        this.distance = this.distance - newSection.distance;
    }

    public void unionDownSection(Section downSection) {
        this.downStation = downSection.getDownStation();
        this.distance += downSection.getDistance();
    }
}