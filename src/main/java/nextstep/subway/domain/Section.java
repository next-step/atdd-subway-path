package nextstep.subway.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;
    @Column(nullable = false)
    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
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

    public void checkEqualsUpStation(final Station downStation) {
        if (this.upStation.isSame(downStation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 지하철역 입니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" + "id=" + id + ", line=" + line + ", upStation=" + upStation + ", downStation=" + downStation + ", distance=" + distance + '}';
    }
}
