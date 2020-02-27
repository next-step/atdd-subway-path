package atdd.station.service;

import atdd.line.domain.Line;
import atdd.line.repository.LineRepository;
import atdd.station.domain.Station;
import atdd.station.dto.PathStation;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WeightedMultigraphFactory {

    private final Map<String, WeightedMultigraph<PathStation, DefaultWeightedEdge>> graphCache = new ConcurrentHashMap<>();

    private final LineRepository lineRepository;

    public WeightedMultigraphFactory(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public WeightedMultigraph<PathStation, DefaultWeightedEdge> create(Station startStation, Station endStation) {
        checkNullStation(startStation, endStation);
        checkEmptyStationLine(startStation, endStation);

        return graphCache.computeIfAbsent("graph", key -> {
            final Set<PathInfo> pathInfos = createPathInfos(lineRepository.findAll());
            return createGraph(pathInfos);
        });
    }

    private void checkNullStation(Station startStation, Station endStation) {
        Assert.notNull(startStation, "출발역은 필수 입니다.");
        Assert.notNull(endStation, "도착역은 필수 입니다.");
    }

    private void checkEmptyStationLine(Station startStation, Station endStation) {
        Assert.isTrue(!CollectionUtils.isEmpty(startStation.getLines()),
                String.format("출발역에 등록된 노선이 없습니다. 역이름 : [%s]", startStation.getName()));
        Assert.isTrue(!CollectionUtils.isEmpty(endStation.getLines()),
                String.format("도착역에 등록된 노선이 없습니다. 역이름 : [%s]", endStation.getName()));
    }

    private Set<PathInfo> createPathInfos(Collection<Line> lines) {
        return lines.stream()
                .map(line -> createLinePathInfos(line, line.getOrderedStations()))
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
    }

    private Stream<PathInfo> createLinePathInfos(Line line, List<Station> stations) {
        return stations.stream()
                .map(station -> createStationPathInfos(line, station))
                .flatMap(Function.identity());
    }

    private Stream<PathInfo> createStationPathInfos(Line line, Station station) {
        return station.getSameLineNextStations(line).stream()
                .map(nextStation -> PathInfo.from(station, nextStation, station.getDistance(line, nextStation)));
    }

    private WeightedMultigraph<PathStation, DefaultWeightedEdge> createGraph(Set<PathInfo> pathInfos) {
        WeightedMultigraph<PathStation, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertexes(graph, pathInfos);
        setEdgeWeights(graph, pathInfos);
        return graph;
    }

    private void addVertexes(WeightedMultigraph<PathStation, DefaultWeightedEdge> graph, Set<PathInfo> pathInfos) {
        for (PathInfo pathInfo : pathInfos) {
            graph.addVertex(pathInfo.currentStation);
            graph.addVertex(pathInfo.nextStation);
        }
    }

    private void setEdgeWeights(WeightedMultigraph<PathStation, DefaultWeightedEdge> graph, Set<PathInfo> pathInfos) {
        for (PathInfo pathInfo : pathInfos) {
            graph.setEdgeWeight(graph.addEdge(pathInfo.currentStation, pathInfo.nextStation), pathInfo.distance);
        }
    }

    private static class PathInfo {

        private PathStation currentStation;
        private PathStation nextStation;
        private double distance;

        private PathInfo() { }

        public static PathInfo from(Station currentStation, Station nextStation, double distance) {
            PathInfo pathInfo = new PathInfo();
            pathInfo.currentStation = PathStation.from(currentStation);
            pathInfo.nextStation = PathStation.from(nextStation);
            pathInfo.distance = distance;
            return pathInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PathInfo)) return false;
            PathInfo pathInfo = (PathInfo) o;
            return Objects.equals(currentStation, pathInfo.currentStation) &&
                    Objects.equals(nextStation, pathInfo.nextStation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentStation, nextStation);
        }

        @Override
        public String toString() {
            return "PathInfo{" +
                    "currentStation=" + currentStation +
                    ", nextStation=" + nextStation +
                    ", distance=" + distance +
                    '}';
        }

    }

}
