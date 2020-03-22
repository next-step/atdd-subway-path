package atdd.path.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class LineRequestView {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    @Builder
    public LineRequestView(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }
}
