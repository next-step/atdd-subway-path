package nextstep.subway.domain;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public boolean hasShorterDistanceThan(Section origin) {
        return this.distance < origin.distance;
    }

    public boolean hasOneMatchedStation(Section section) {
        return this.upStation.equals(section.downStation)
                || this.downStation.equals(section.upStation)
                || this.upStation.equals(section.upStation)
                || this.downStation.equals(section.downStation);
    }

    public boolean hasBothMatchedStation(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean hasNoneMatchedStation(Section section) {
        return !this.hasBothMatchedStation(section);
    }

    public Section divideBy(Section section) {
        if(this.downStation.equals(section.downStation)){
            return new Section(this.line, this.upStation, section.upStation, this.distance - section.distance);
        }
        return new Section(this.line, section.downStation, this.downStation, this.distance - section.distance);
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
