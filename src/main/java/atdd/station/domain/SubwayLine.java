package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class SubwayLine {
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
    private String interval;

    @Embedded
    private Station station = new Station();

    private boolean deleted = false;

    public SubwayLine() {
    }

    @Builder
    public SubwayLine(String name, String startTime, String endTime, String interval, Station station) {
        this.name = name;
    }

    @Builder
    public SubwayLine(String name) {
        this.name = name;
        this.startTime = "05:00";
        this.endTime = "23:50";
        this.interval = "10";
        this.station = new Station();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void deleteSubwayLine() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public String toString() {
        return "SubwayLine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", interval='" + interval + '\'' +
                ", station=" + station +
                ", deleted=" + deleted +
                '}';
    }

}


