package atdd.domain.stations;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

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

    private boolean isExist=false;

    @OneToOne(mappedBy = "source")
    private Section sectionAsSource;

    @OneToOne(mappedBy = "target")
    private Section sectionAsTarget;

    @Builder
    public Stations(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines=stationLines;
        this.isExist=true;
    }

    public Stations(String name){
        this.name=name;
        this.isExist=true;
    }

    public List<Line> getLines(){
        return this.stationLines
                .stream()
                .map(StationLine::getLine)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id){
        this.isExist=false;
    }
}
