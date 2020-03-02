package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "line_id")
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;

    @OneToMany(mappedBy = "line")
    private List<StationLine> stationLines;

    public Line() {
    }

    @Builder
    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int interval,
                List<StationLine> stationLines) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.stationLines = stationLines;
    }

    public void changeStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void changeEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void changeInterval(int interval) {
        this.interval = interval;
    }
}
