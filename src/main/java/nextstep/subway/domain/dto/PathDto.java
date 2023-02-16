package nextstep.subway.domain.dto;

import java.util.Collections;
import java.util.List;
import nextstep.subway.domain.Station;

public class PathDto {

    private List<Station> nodes;
    private double weight;

    public PathDto(final List<Station> nodes, final double weight) {
        this.nodes = nodes;
        this.weight = weight;
    }

    public List<Station> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public double getWeight() {
        return weight;
    }
}
