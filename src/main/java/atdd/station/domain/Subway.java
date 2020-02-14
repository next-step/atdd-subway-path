package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;

@Entity
public class Subway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "subwayLine_id")
    private SubwayLine subwayLine;

    private boolean deleted = false;

    public Subway() {
    }

    boolean isThisNameTheStation(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return name.equals(this.station.getName());
    }

    public Station getStation() {
        return this.station;
    }

    public SubwayLine getSubwayLine() {
        return this.subwayLine;
    }

    @Builder
    public Subway(Station station, SubwayLine subwayLine) {
        this.station = station;
        this.subwayLine = subwayLine;
    }
}


