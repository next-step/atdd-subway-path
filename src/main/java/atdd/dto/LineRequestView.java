package atdd.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class LineRequestView {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;

    public LineRequestView() {
    }

    @Builder
    public LineRequestView(String name, LocalTime startTime, LocalTime endTime, int interval) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
    }
}
