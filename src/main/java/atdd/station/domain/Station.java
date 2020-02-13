package atdd.station.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Station
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "station_id")
    //@EmbeddedId
    private Integer id;

    @Column(length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "line")
    @OrderBy("id ASC")
    private List<Subway> lines = new ArrayList<>();

    @OneToOne(mappedBy = "source")
    private Edge source;

    @OneToOne(mappedBy = "target")
    private Edge target;

    @Builder
    public Station(String name, List<Subway> lines)
    {
        this.name = name;
        this.lines = lines;
    }

    public Station(String name)
    {
        this.name = name;
    }
}
