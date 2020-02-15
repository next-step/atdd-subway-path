package atdd.line;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class LineCreateRequest {
    private String name;
    private LocalTime start_time;
    private LocalTime end_time;
    private int interval_time;
    private int extra_fare;

    @Builder
    public LineCreateRequest(String name, LocalTime start_time, LocalTime end_time, int interval_time, int extra_fare) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.interval_time = interval_time;
        this.extra_fare = extra_fare;
    }

    public Line toEntity() {
        return Line.builder()
                .name(name)
                .start_time(start_time)
                .end_time(end_time)
                .interval_time(interval_time)
                .extra_fare(extra_fare)
                .build();
    }
}
