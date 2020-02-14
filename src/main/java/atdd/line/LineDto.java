package atdd.line;

import atdd.station.domain.dto.Item;
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
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private List<Item> stations;

    public static LineDto of(Line line) {
        return LineDto.builder()
                .id(line.getId())
                .name(line.getName())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .stations(line.getStations().stream()
                        .map(it -> Item.of(it.getId(), it.getName()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<LineDto> listOf(List<Line> lines) {
        return lines.stream()
                .map(it -> LineDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .startTime(it.getStartTime())
                        .endTime(it.getEndTime())
                        .intervalTime(it.getIntervalTime())
                        .stations(it.getStations().stream()
                                .map(it2 -> Item.of(it2.getId(), it2.getName()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public Line toLine() {
        return Line.builder()
                .id(this.id)
                .name(this.name)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .intervalTime(this.intervalTime)
                .build();
    }
}
