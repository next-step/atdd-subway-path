package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeRequestView {
    private Long id;
    private Line line;
    private Station source;
    private Station target;
    private int distance;
    private int timeToTake;

    public EdgeRequestView() {
    }

    @Builder
    public EdgeRequestView(Long id, Line line, Station source,
                           Station target, int distance, int timeToTake) {
        this.id = id;
        this.line = line;
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.timeToTake = timeToTake;
    }
}
