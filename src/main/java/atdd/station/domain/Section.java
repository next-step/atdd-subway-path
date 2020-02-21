package atdd.station.domain;

import atdd.line.domain.Line;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, updatable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false, updatable = false)
    private Station station;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_station_id", nullable = false)
    private Station nextStation;

    @Embedded
    private Duration duration;

    @Column(name = "distance", scale = 2)
    private double distance;

    protected Section() { }

    public static Section create(Line line, Station station, Station nextStation, Duration duration, double distance) {
        Assert.notNull(line, "line 은 필수값 입니다.");
        Assert.notNull(station, "station 은 필수값 입니다.");
        Assert.notNull(nextStation, "nextStation 은 필수값 입니다.");
        Assert.notNull(duration, "duration 은 필수값 입니다.");
        Assert.isTrue(distance > 0, "distance 는 0보다 커야 합니다.");

        final Section section = new Section();
        section.line = line;
        section.station = station;
        section.nextStation = nextStation;
        section.duration = duration;
        section.distance = distance;
        return section;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isEqualNextSection(String lineName, String stationName) {
        return isEqualLine(lineName) && isEqualNextStation(stationName);
    }

    public boolean isEqualLine(String lineName) {
        return Objects.equals(lineName, line.getName());
    }

    private boolean isEqualNextStation(String stationName) {
        return Objects.equals(stationName, nextStation.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
