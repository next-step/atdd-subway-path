package nextstep.subway.path.application;

import nextstep.subway.path.domain.SubwayGraph;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private SubwayGraph subwayGraph;

    public GraphService(SubwayGraph subwayGraph) {
        this.subwayGraph = subwayGraph;
    }

    public SubwayGraph findGraph() {
        return subwayGraph;
    }
}
