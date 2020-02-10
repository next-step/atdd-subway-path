package atdd.line;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<LineDto> listOf(List<Line> lines) {
        return lines.stream()
                .map(it -> LineDto.builder()
                        .name(it.getName())
                        .startTime(it.getStartTime())
                        .endTime(it.getEndTime())
                        .intervalTime(it.getIntervalTime())
                        .build())
                .collect(Collectors.toList());
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
