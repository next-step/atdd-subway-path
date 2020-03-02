package atdd.path.application.dto;

import atdd.path.domain.Line;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class LineRequestView {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;

    public LineRequestView() {
    }

    @Builder
    public LineRequestView(Long id, String name, LocalTime startTime, LocalTime endTime, int interval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
    }

    public Line toLine(){
        return Line.builder()
                .name(name)
                .startTime(startTime)
                .endTime(endTime)
                .interval(interval)
                .build();
    }

    public static LineRequestView of(Line line){
        return LineRequestView.builder()
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .interval(line.getInterval())
                .build();
    }
}
