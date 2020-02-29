package atdd.domain.stations;

import com.sun.istack.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity(name = "line")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String startTime="05:00";

    @Column(nullable = false)
    private String endTime="23:50";

    @Column(nullable = false)
    private String intervalTime="10";

    @OneToMany(mappedBy = "stations")
    private List<StationLine> stationLines=new ArrayList<>();

    @Builder
    public Line(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines = stationLines;
    }

    public Line(String name){
        this.name=name;
    }

    public List<Stations> getStations(){
        return this.stationLines
                .stream()
                .map(StationLine::getStations)
                .collect(Collectors.toList());
    }
}
