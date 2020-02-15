package atdd.line.domain;

import atdd.station.domain.Station;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "line")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Embedded
    private TimeTable timeTable;

    @Column(name = "intervalTime")
    private int intervalTime;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line() {
    }

    public static Line create(String name, TimeTable timeTable, int intervalTime) {
        Assert.hasText(name, "name 은 필수값 입니다.");
        Assert.notNull(timeTable, "timeTable 은 필수값 입니다.");
        Assert.isTrue(intervalTime >= 0, "intervalTime 은 음수일 수 없습니다.");
        return of(null, name, timeTable, intervalTime);
    }

    public static Line of(Long id, String name, TimeTable timeTable, int intervalTime) {
        Line line = new Line();
        line.id = id;
        line.name = name;
        line.timeTable = timeTable;
        line.intervalTime = intervalTime;
        return line;
    }

    public List<Station> getStations() {
        return this.lineStations.stream().map(LineStation::getStation).collect(Collectors.toList());
    }

    public void addStation(Station station) {
        if (existStation(station)) {
            throw new IllegalArgumentException("이미 존재하는 역입니다. 역이름 : [" + station.getName() + "]");
        }
        LineStation lineStation = new LineStation(this, station);
        this.lineStations.add(lineStation);
    }

    private boolean existStation(Station station) {
        return lineStations.stream().anyMatch(lineStation -> lineStation.isEqualStation(station.getName()));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
