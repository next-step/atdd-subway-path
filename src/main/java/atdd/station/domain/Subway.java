package atdd.station.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
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

    @Builder
    public Subway(Station station, SubwayLine subwayLine) {
        this.station = station;
        this.subwayLine = subwayLine;
    }
}


