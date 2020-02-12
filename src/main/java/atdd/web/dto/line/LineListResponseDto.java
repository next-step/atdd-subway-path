package atdd.web.dto.line;

import atdd.domain.stations.Line;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class LineListResponseDto {
    public List<Line> lines;

    @Builder
    public LineListResponseDto(List<Line> lines){
        this.lines=lines;
    }

    public LineListResponseDto toDtoEntity(List<Line> lines) {
        return LineListResponseDto.builder()
                .lines(lines)
                .build();
    }
}
