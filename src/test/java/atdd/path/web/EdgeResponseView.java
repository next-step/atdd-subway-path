package atdd.path.web;

import atdd.path.domain.Line;
import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeResponseView {
    private Long id;
    private Station source;
    private Station target;
    private Line line;

    public EdgeResponseView() {
    }

    @Builder
    public EdgeResponseView(Long id, Station source, Station target, Line line) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.line = line;
    }
}
