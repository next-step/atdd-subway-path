package atdd.line;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class LineListResponse {
    private List<Line> lines;

    @Builder
    public LineListResponse(List<Line> lines){
        this.lines = lines;
    }

    public static LineListResponse of(List<Line> lines){
        return LineListResponse.builder()
                .lines(lines)
                .build();
    }

}
