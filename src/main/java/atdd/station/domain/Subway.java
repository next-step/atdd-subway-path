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
    @JoinColumn(name = "subway_line_id")
    private SubwayLine subwayLine;

    private boolean deleted = false;

    public Subway() {
    }

    @Builder
    public Subway(Station station, SubwayLine subwayLine) {
        this.station = station;
        this.subwayLine = subwayLine;
    }

    public SubwayLine getSubwayLine() {
        return this.subwayLine;
    }
}


