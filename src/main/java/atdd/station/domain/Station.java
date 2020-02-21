package atdd.station.domain;

import atdd.line.domain.Line;
import atdd.line.domain.LineStation;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "station", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Station() {
    }

    public static Station create(String name) {
        Assert.hasText(name, "name은 필수 입니다.");
        Station station = new Station();
        station.name = name;
        return station;
    }

    public void addLine(Line line) {
        if (existLine(line)) {
            throw new IllegalArgumentException("등록된 line 입니다. lineName : [" + line.getName() + "]");
        }
        final LineStation lineStation = new LineStation(line, this);
        this.lineStations.add(lineStation);
    }

    private boolean existLine(Line line) {
        return this.lineStations.stream().anyMatch(lineStation -> lineStation.isEqualLine(line.getName()));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return this.lineStations.stream()
                .map(LineStation::getLine)
                .collect(Collectors.toList());
    }

    public void addNextStation(Line line, Station nextStation, Duration duration, double distance) {
        checkExistSameLineNextStation(line, nextStation);

        Section section = Section.create(line, this, nextStation, duration, distance);
        this.sections.add(section);
    }

    public boolean existSameLineNextStation(Line line, Station nextStation) {
        return getSameLineNextStations(line).contains(nextStation);
    }

    private void checkExistSameLineNextStation(Line line, Station target) {
        if (existSameLineNextStation(line, target)) {
            throw new IllegalArgumentException(String.format("동일한 다음역이 존재합니다. lineName : [%s], nextStationName : [%s]",
                    line.getName(), target.getName()));
        }
    }

    public Set<Station> getSameLineNextStations(Line line) {
        return sections.stream()
                .filter(section -> section.isEqualLine(line.getName()))
                .map(Section::getNextStation)
                .collect(Collectors.toSet());
    }

    public boolean isEqualStation(Long stationId) {
        return Objects.equals(id, stationId);
    }

    public Duration getDuration(Line line, Station station) {
        return getSection(line, station).getDuration();
    }

    public double getDistance(Line line, Station station) {
        return getSection(line, station).getDistance();
    }

    private Section getSection(Line line, Station station) {
        final String lineName = line.getName();
        final String stationName = station.getName();
        return sections.stream()
                .filter(section -> section.isEqualNextSection(lineName, stationName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "등록된 구간이 없습니다. currentStation : [%s], lineName : [%s], stationName : [%s]",
                        this.name, lineName, stationName)));
    }

    public void deleteSection(Line line, Station target) {
        final String lineName = line.getName();
        final String stationName = target.getName();
        final boolean remove = sections.removeIf(section -> section.isEqualNextSection(lineName, stationName));
        if (!remove) {
            throw new IllegalArgumentException(String.format(
                    "등록된 다음역이 없습니다. currentStation : [%s], lineName : [%s], stationName : [%s]",
                    name, lineName, stationName));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
