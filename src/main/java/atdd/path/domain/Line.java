package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private Edges edges;


    public Line() {
    }

    @Builder
    public Line(Long id, String name, List<Edge> edges, LocalTime startTime, LocalTime endTime, int interval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = interval;
        this.edges = new Edges(edges);
    }

    public void changeStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void changeEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void changeInterval(int interval) {
        this.intervalTime = interval;
    }
}
