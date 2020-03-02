package atdd.path.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "line_id")
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int interval;

    @OneToMany(mappedBy = "line")
    private List<StationLine> stationLines;
}
