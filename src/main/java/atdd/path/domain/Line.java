package atdd.path.domain;

import atdd.path.dto.LineRequestView;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    @Builder
    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }


    public static Line of(LineRequestView requestView) {
        return Line.builder()
                .name(requestView.getName())
                .startTime(requestView.getStartTime())
                .endTime(requestView.getEndTime())
                .intervalTime(requestView.getIntervalTime())
                .build();
    }
}
