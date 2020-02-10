package atdd.line;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@EqualsAndHashCode
public class LineDto {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    public static LineDto of(Line line) {
        return LineDto.builder()
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .build();
    }

    public Line toLine() {
        return Line.builder()
                .name(this.name)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .intervalTime(this.intervalTime)
                .build();
    }
}
