package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jgrapht.GraphPath;

import java.util.List;

@Getter
@AllArgsConstructor
public class Path {

    private List<Station> stations;
    private int distance;

    public Path(GraphPath graphPath) {
        this.stations = graphPath.getVertexList();
        this.distance = (int) graphPath.getWeight();
    }

}
