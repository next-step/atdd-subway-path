package atdd.domain.stations;

import com.sun.istack.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "stations")
    private List<StationLine> stationLines=new ArrayList<>();

    @Builder
    public Line(String name, List<StationLine> stationLines){
        this.name=name;
        this.stationLines=stationLines;
    }

    public Line(String name){
        this.name=name;
    }
}
