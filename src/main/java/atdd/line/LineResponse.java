package atdd.line;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private LocalTime start_time;
    private LocalTime end_time;
    private int interval_time;
    private int extra_fare;

    public LineResponse(){

    }

    @Builder
    public LineResponse(Line entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.start_time = entity.getStart_time();
        this.end_time = entity.getEnd_time();
        this.interval_time = entity.getInterval_time();
        this.extra_fare = entity.getExtra_fare();
    }

    public static LineResponse of(Line enity){
        return LineResponse.builder()
                .entity(enity)
                .build();
    }
}
