package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;

@Entity
public class Subway
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Builder
    public Subway(Station station, Line line)
    {
        this.station = station;
        this.line = line;
    }

}
