package atdd.path.application.dto;

import atdd.path.domain.Line;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class LineListResponseView {
    private Long id;
    private List<Line> lines;

    public LineListResponseView() {
    }

    @Builder
    public LineListResponseView(Long id, List<Line> lines) {
        this.id = id;
        this.lines = lines;
    }
}
