package atdd.domain.stations;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "stationline")
public class StationLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stationline_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Stations stations;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Builder
    public StationLine(Stations stations, Line line){
        this.stations=stations;
        this.line=line;
    }

    public Line getLine(){
        return this.line;
    }
}
