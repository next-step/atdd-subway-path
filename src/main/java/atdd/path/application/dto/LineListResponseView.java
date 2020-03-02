package atdd.path.application.dto;

import atdd.path.domain.Line;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class LineListResponseView {
    private List<Line> lines;

    public LineListResponseView() {
    }

    @Builder
    public LineListResponseView(List<Line> lines) {
        this.lines = lines;
    }
}
