package atdd.station.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Entity
@Embeddable
@IdClass(Station.class)
public class Edge implements Serializable
{
    private static final long ELAPSED_TIME = 10;
    private static final Double DISTANCE = 5.0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "edge_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Id
    @Column(length = 20, nullable = false)
    private long elapsedTime;

    @Id
    @Column(length = 20, nullable = false)
    private Double distance;

    @OneToOne
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private Station source;

    @OneToOne
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private Station target;

    @Builder
    public Edge(Line line, Station source, Station target)
    {
        this.line = line;
        this.elapsedTime = ELAPSED_TIME;
        this.distance = DISTANCE;
        this.source = source;
        this.target = target;
    }

}




