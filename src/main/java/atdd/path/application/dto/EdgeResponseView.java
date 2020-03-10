package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeResponseView {
    private Long id;

    private Station source;

    private Station target;

    @JsonIgnore
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

    public static EdgeResponseView of(Edge edge){
        return EdgeResponseView.builder()
                .id(edge.getId())
                .line(edge.getLine())
                .source(edge.getSource())
                .target(edge.getTarget())
                .build();
    }
}
