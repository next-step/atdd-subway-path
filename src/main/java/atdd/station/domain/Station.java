package atdd.station.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Station
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Builder
    public Station(String name)
    {
        this.name = name;
    }

}
