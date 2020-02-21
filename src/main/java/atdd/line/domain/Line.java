package atdd.line.domain;

import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "line")
public class Line {

    public static final int DELETABLE_SIZE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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
        if (Objects.isNull(startLineStation)) {
            changeStartStation(station);
        }

        station.addNextStation(this, nextStation, duration, distance);
        nextStation.addNextStation(this, station, duration, distance);
    }

    private void changeStartStation(Station station) {
        this.startLineStation = getLineStation(station);
    }

    private LineStation getLineStation(Station station) {
        final String stationName = station.getName();
        return this.lineStations.stream()
                .filter(lineStation -> lineStation.isEqualStation(stationName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다. name : [" + stationName + "]"));
    }

    private Station getStation(Long stationId) {
        return getStations().stream()
                .filter(station -> station.isEqualStation(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 station 입니다. stationId : [" + stationId + "]"));
    }

    public void deleteStation(Long stationId) {
        final Station deleteTargetStation = getStation(stationId);
        final Set<Station> nextStations = deleteTargetStation.getSameLineNextStations(this);
        if (CollectionUtils.isEmpty(nextStations)) {
            return;
        }

        checkDeletableSize(nextStations);

        final Duration sumOfDuration = sumDuration(nextStations, deleteTargetStation);
        final double sumOfDistance = sumDistance(nextStations, deleteTargetStation);
        joinStations(nextStations, sumOfDuration, sumOfDistance);

        if (isStartStation(deleteTargetStation)) {
            changeStartStation(CollectionUtils.firstElement(nextStations));
        }

        deleteSections(nextStations, deleteTargetStation);
        removeLineStation(deleteTargetStation);
    }

    private boolean isStartStation(Station deleteTargetStation) {
        if (startLineStation == null) {
            return false;
        }
        return Objects.equals(startLineStation.getStation(), deleteTargetStation);
    }

    private void checkDeletableSize(Set<Station> nextStations) {
        if (nextStations.size() > DELETABLE_SIZE) {
            throw new IllegalArgumentException(nextStations.size() + "개 역이 연결되어 있습니다. 삭제는 2개일때만 가능합니다.");
        }
    }

    private void deleteSections(Set<Station> nextStations, Station deleteTargetStation) {
        for (Station nextStation : nextStations) {
            nextStation.deleteSection(this, deleteTargetStation);
            deleteTargetStation.deleteSection(this, nextStation);
        }
    }

    private void joinStations(Set<Station> stations, Duration duration, double distance) {
        final Station beforeStation = CollectionUtils.firstElement(stations);
        final Station afterStation = CollectionUtils.lastElement(stations);

        if (Objects.equals(beforeStation, afterStation)) {
            return;
        }

        beforeStation.addNextStation(this, afterStation, duration, distance);
        afterStation.addNextStation(this, beforeStation, duration, distance);
    }

    private double sumDistance(Set<Station> nextStations, Station deleteTargetStation) {
        return nextStations.stream()
                .map(station -> station.getDistance(this, deleteTargetStation))
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalArgumentException("nextStations 의 size 가 0 입니다."));
    }

    private Duration sumDuration(Set<Station> nextStations, Station deleteTargetStation) {
        return nextStations.stream()
                .map(station -> station.getDuration(this, deleteTargetStation))
                .reduce(Duration::add)
                .orElseThrow(() -> new IllegalArgumentException("nextStations 의 size 가 0 입니다."));
    }

    private void removeLineStation(Station deleteTargetStation) {
        lineStations.removeIf(lineStation -> lineStation.isEqualStation(deleteTargetStation.getName()));
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
