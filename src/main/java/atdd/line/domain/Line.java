package atdd.line.domain;

import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "line")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Embedded
    private TimeTable timeTable;

    @Column(name = "interval_time")
    private int intervalTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_line_station_id")
    private LineStation startLineStation;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line() {
    }

    public static Line create(String name, TimeTable timeTable, int intervalTime) {
        Assert.hasText(name, "name 은 필수값 입니다.");
        Assert.notNull(timeTable, "timeTable 은 필수값 입니다.");
        Assert.isTrue(intervalTime >= 0, "intervalTime 은 음수일 수 없습니다.");
        Line line = new Line();
        line.name = name;
        line.timeTable = timeTable;
        line.intervalTime = intervalTime;
        return line;
    }

    public List<Station> getStations() {
        return this.lineStations.stream().map(LineStation::getStation).collect(Collectors.toList());
    }

    public List<Station> getOrderedStations() {
        final Optional<Station> startStationOptional = getStartStation();
        if (!startStationOptional.isPresent()) {
            return Collections.emptyList();
        }

        Set<Station> stations = new LinkedHashSet<>();
        final Station station = startStationOptional.get();
        stations.add(station);
        addNextStations(stations, this, station);

        return new ArrayList<>(stations);
    }

    private void addNextStations(Set<Station> stations, Line line, Station currentStation) {
        final Set<Station> nextStations = currentStation.getSameLineNextStations(line);
        if (nextStations.isEmpty()) {
            return;
        }

        for (Station nextStation : nextStations) {
            if (stations.contains(nextStation)) {
                continue;
            }
            stations.add(nextStation);
            addNextStations(stations, line, nextStation);
        }
    }

    public void addStation(Station station) {
        if (existStation(station)) {
            throw new IllegalArgumentException("이미 존재하는 역입니다. 역이름 : [" + station.getName() + "]");
        }

        this.lineStations.add(new LineStation(this, station));
        if (Objects.isNull(startLineStation)) {
            changeStartStation(station);
        }
    }

    public void changeStartStation(Station station) {
        this.startLineStation = getLineStation(station);
    }

    private LineStation getLineStation(Station station) {
        final String stationName = station.getName();
        return this.lineStations.stream()
                .filter(lineStation -> lineStation.isEqualStation(stationName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다. name : [" + stationName + "]"));
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

    public Optional<Station> getStartStation() {
        if (Objects.isNull(startLineStation)) {
            return Optional.empty();
        }
        return Optional.of(startLineStation.getStation());
    }

    public boolean isSameName(String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        return this.name.equals(name);
    }

    public void addSection(Long stationId, Long nextStationId, Duration duration, double distance) {
        final Station station = getStation(stationId);
        final Station nextStation = getStation(nextStationId);

        station.addNextStation(this, nextStation, duration, distance);
        nextStation.addNextStation(this, station, duration, distance);
    }

    private Station getStation(Long stationId) {
        return getStations().stream()
                .filter(station -> station.isEqualStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 station 입니다. stationId : [" + stationId + "]"));
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

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeTable=" + timeTable +
                ", intervalTime=" + intervalTime +
                '}';
    }

}
