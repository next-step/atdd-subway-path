package atdd.path.domain;

import atdd.path.application.dto.LineResponseView;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;

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
    private Integer intervalTime;


    public Line() {
    }

    @Builder
    public Line(Long id, String name, LocalTime startTime,
                LocalTime endTime, int interval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = interval;
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

    public static Line of(LineResponseView responseView){
        return Line.builder()
                .id(responseView.getId())
                .name(responseView.getName())
                .startTime(responseView.getStartTime())
                .endTime(responseView.getEndTime())
                .interval(responseView.getInterval())
                .build();
    }
}
