package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.domain.exception.SectionException;

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

    private boolean head;

    private boolean tail;

    public Section() {

    }


    public Section(Line line, Station upStation, Station downStation, int distance, boolean head,
        boolean tail) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.head = head;
        this.tail = tail;
    }

    public static Section createHeadAndTailSection(Line line, Station upStation,
        Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, true, true);
    }

    public static Section createHeadSection(Line line, Station upStation, Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, true, false);
    }

    public static Section createTailSection(Line line, Station upStation, Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, false, true);
    }

    public static Section createMiddleSection(Line line, Station upStation, Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, false, false);
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

    public boolean isHead() {
        return head;
    }

    public boolean isTail() {
        return tail;
    }

    public void changeUpStation(Station downStation) {
        this.upStation = downStation;
    }

    public void changeDistance(int distance) {
        if (distance <= 0) {
            throw new SectionException(SectionException.INVALID_DISTANCE);
        }
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && head == section.head && tail == section.tail
            && Objects.equals(id, section.id) && Objects.equals(line, section.line)
            && Objects.equals(upStation, section.upStation) && Objects.equals(
            downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance, head, tail);
    }

    public void changeTail(boolean tail) {
        this.tail = tail;
    }

    public void changeHead(boolean head) {
        this.head = head;
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }
}
