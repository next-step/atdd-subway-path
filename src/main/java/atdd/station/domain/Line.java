package atdd.station.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Line
{
    private static final String START_TIME = "05:00";
    private static final String END_TIME = "23:50";
    private static final String INTERVAL_TIME = "10";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String startTime;

    @Column(length = 20, nullable = false)
    private String endTime;

    @Column(length = 20, nullable = false)
    private String intervalTime;

    @OneToMany(mappedBy = "station")
    @OrderBy("id ASC")
    private List<Subway> subways = new ArrayList<>();

    @Builder
    public Line(String name, List<Subway> subways)
    {
        this.name = name;
        this.startTime = START_TIME;
        this.endTime = END_TIME;
        this.intervalTime = INTERVAL_TIME;
        this.subways = subways;
    }

}
