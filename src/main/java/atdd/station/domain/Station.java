package atdd.station.domain;

import atdd.line.domain.Line;
import atdd.line.domain.LineStation;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    protected Station() { }

    public static Station create(String name) {
        Assert.hasText(name, "name은 필수 입니다.");
        return of(null, name);
    }

    public static Station of(Long id, String name) {
        Station station = new Station();
        station.id = id;
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

    public List<Line> getLines() {
        return this.lineStations.stream()
                .map(LineStation::getLine)
                .collect(Collectors.toList());
    }

}
