package atdd.station.domain;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class SubwayLine {
    private static final String DEFAULT_START_TIME = "05:00";
    private static final String DEFAULT_END_TIME = "23:50";
    private static final String DEFAULT_INTERVAL = "10";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    @Size(min = 4, max = 10)
    private String startTime;

    @Size(min = 4, max = 10)
    private String endTime;

    @Size(min = 2, max = 20)
    private String intervalTime;

    @OneToMany(mappedBy = "station", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Subway> subways = new ArrayList<>();

    private boolean deleted = false;

    public SubwayLine() {
    }

    @Builder
    public SubwayLine(String name, String startTime, String endTime, String intervalTime, List<Subway> subways) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.startTime = startTime;
        this.subways = subways;
    }

    public SubwayLine(String name) {
        this.name = name;
        this.startTime = DEFAULT_START_TIME;
        this.endTime = DEFAULT_END_TIME;
        this.intervalTime = DEFAULT_INTERVAL;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getIntervalTime() {
        return intervalTime;
    }

    public void deleteSubwayLine() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public List<Subway> getSubways() {
        return this.subways;
    }

    public List<Station> getStations() {
        return this.subways.stream()
                .map(Subway::getStation)
                .collect(Collectors.toList());
    }

    public SubwayLine updateSubwayByStations(List<Station> stations) {
        this.subways.addAll(makeSubwaysByStations(stations));
        return this;
    }

    List<Subway> makeSubwaysByStations(List<Station> stations) {
        return stations.stream()
                .map(station -> new Subway(station, this))
                .collect(Collectors.toList());
    }

    public void deleteStationByName(String stationName) {
        Station station = getStationByName(stationName);
        station.deleteStation();
    }

    public Station getStationByName(String stationName) {
        return this.subways.stream()
                .filter(subway -> subway.isThisNameTheStation(stationName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getStation();
    }

    @Override
    public String toString() {
        return "SubwayLine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", interval='" + intervalTime + '\'' +
                ", station=" + subways +
                ", deleted=" + deleted +
                '}';
    }
}


