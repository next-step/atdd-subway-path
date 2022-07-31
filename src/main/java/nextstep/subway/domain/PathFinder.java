package nextstep.subway.domain;

import java.util.List;

public class PathFinder<T> {
    CommonGraph graph;

    public PathFinder(List<T> datas) {
        graph = getGraph(datas);
    }

    private CommonGraph getGraph(List<T> graphDatas) {
        if (graphDatas.isEmpty()) {
            throw new IllegalArgumentException("HAVE_NO_DATA");
        }
        if (graphDatas.get(0).getClass().equals(Line.class)) {
            return new SubwayGraph((List<Line>) graphDatas);
        }
        return null;
    }

    public List<Station> getShortestPath(Station source, Station target) {
        return graph.getShortestPath(source, target);
    }

    public int getShortestDistance(Station source, Station target) {
        return graph.getShortestDistance(source, target);
    }
}
