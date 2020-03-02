package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeResponseView {
    private Long id;
    private Line line;
    private Station source;
    private Station target;


    public EdgeResponseView() {
    }

    @Builder
    public EdgeResponseView(Long id, Line line, Station source, Station target) {
        this.id = id;
        this.line = line;
        this.source = source;
        this.target = target;
    }
}
