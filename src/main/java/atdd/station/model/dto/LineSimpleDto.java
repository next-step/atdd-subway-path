package atdd.station.model.dto;

import atdd.station.model.entity.Line;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LineSimpleDto {
    private long id;
    private String name;

    public LineSimpleDto() {
    }

    @Builder
    private LineSimpleDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<LineSimpleDto> of(List<Line> lines) {
        return lines.stream().map(data -> LineSimpleDto.builder()
                .id(data.getId())
                .name(data.getName()).build())
                .collect(Collectors.toList());
    }
}
