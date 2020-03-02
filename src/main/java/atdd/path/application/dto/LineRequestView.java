package atdd.path.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
}
