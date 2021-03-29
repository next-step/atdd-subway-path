package nextstep.subway.path.application;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GraphService {

    //그래프 생성
    public SubwayGraph findGraph(List<Section> sections, List<Station> stations){

        SubwayGraph graph = new SubwayGraph();

        stations.forEach(graph::addVertex);

        for (Section section : sections) {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance());
        }

        return graph;
    }

    //라인 조회
    public List<Station> makeStations(List<Section> sections){
        return sections.stream()
                .flatMap(section -> Stream.of(
                        section.getUpStation(),
                        section.getDownStation()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

}
