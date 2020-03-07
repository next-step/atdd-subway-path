package atdd.line.api.response;

import atdd.line.domain.Line;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class LinesResponseView {

    private int count;
    private List<LineResponseView> lines;

    public LinesResponseView(List<Line> lines) {
        this.count = lines.size();
        this.lines = lines.stream().map(LineResponseView::new).collect(toList());
    }

}
