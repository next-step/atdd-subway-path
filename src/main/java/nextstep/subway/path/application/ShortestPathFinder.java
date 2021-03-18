package nextstep.subway.path.application;

import nextstep.subway.line.dto.LineSectionResponse;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathFinder {

    WeightedMultigraph<String, DefaultWeightedEdge> graph;
    List<StationResponse> stationResponses;

    public ShortestPathFinder(List<StationResponse> stationResponses, List<LineSectionResponse> lineSectionResponses) {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.stationResponses = stationResponses;
        setStations(stationResponses);
        setSections(lineSectionResponses);
    }

    public void setStations(List<StationResponse> stationResponses) {
        for (StationResponse stationResponse : stationResponses) {
            graph.addVertex(String.valueOf(stationResponse.getId()));
        }
    }

    public void setSections(List<LineSectionResponse> lineSectionResponses) {
        for (LineSectionResponse lineSectionResponse : lineSectionResponses) {
            List<SectionResponse> sectionResponses = lineSectionResponse.getSections();
            for (SectionResponse sectionResponse : sectionResponses) {
                graph.setEdgeWeight(
                        graph.addEdge(String.valueOf(sectionResponse.getUpStationId()), String.valueOf(sectionResponse.getDownStationId())),
                        sectionResponse.getDistance()
                );
            }
        }
    }

    public List<StationResponse> getShortestPath(Long sourceStationId, Long targetStationId) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(String.valueOf(sourceStationId), String.valueOf(targetStationId)).getVertexList();
        return shortestPath.stream()
                .map(id -> stationResponses.stream().filter(it -> it.getId() == Long.parseLong(id)).findFirst().orElseThrow(RuntimeException::new))
                .collect(Collectors.toList());
    }

    public int getShortestDistance(Long sourceStationId, Long targetStationId) {
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(String.valueOf(sourceStationId), String.valueOf(targetStationId));
        return  (int) paths.get(0).getWeight();
    }
}
