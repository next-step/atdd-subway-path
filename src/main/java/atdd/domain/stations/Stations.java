package atdd.domain.stations;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity(name = "station")
public class Stations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "line")
    private List<StationLine> stationLines=new ArrayList<>();

    @Builder
    public Stations(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines=stationLines;
    }

    public Stations(String name){
        this.name=name;
    }

    public List<Line> getLines(){
        return this.stationLines
                .stream()
                .map(StationLine::getLine)
                .collect(Collectors.toList());
    }
}
