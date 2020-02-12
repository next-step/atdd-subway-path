package atdd.station.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "station")
public class Station
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "line")
    @OrderBy("id ASC")
    private List<Subway> lines = new ArrayList<>();

    @Builder
    public Station(String name, List<Subway> lines)
    {
        this.name = name;
        this.lines = lines;
    }

}
