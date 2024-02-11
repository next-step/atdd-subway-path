package nextstep.subway.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section implements Comparable<Section> {

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

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(final Station station) {
        return this.downStation.equals(station);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void reduceDistance(int distance) {
        if (this.distance <= distance) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중간에 추가되는 길이가 상행역의 길이보다 크거나 같을 수 없습니다.");
        }

        this.distance = this.distance - distance;
    }

    public void plusDistance(int distance) {
        this.distance = this.distance + distance;
    }

    public Station[] getStations() {
        return new Station[]{this.upStation, this.downStation};
    }

    @Override
    public int compareTo(Section otherSection) {
        return Objects.equals(this.downStation.getId(), otherSection.getUpStation().getId()) ? -1 : 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStation.getId(), section.upStation.getId()) && Objects.equals(downStation.getId(), section.downStation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation.getId(), downStation.getId(), distance);
    }
}
