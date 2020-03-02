package atdd.path.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class LineRequestView {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int interval;

    public LineRequestView() {
    }

    @Builder
    public LineRequestView(Long id, String name, LocalDateTime startTime, LocalDateTime endTime, int interval) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
    }
}
