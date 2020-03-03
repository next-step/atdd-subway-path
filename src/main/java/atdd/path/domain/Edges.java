package atdd.path.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Edges {
    private List<Edge> edges = new ArrayList<>();

    public Edges() {
    }

    public Edges(List<Edge> edges) {
        checkValidEdges(edges);
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    private void checkValidEdges(List<Edge> edges) {
        if (edges.size() == 0) {
            return;
        }

        if (getStations(edges).size() != edges.size() + 1) {
            throw new RuntimeException();
        }
    }

    public List<Station> getStations() {
        return getStations(this.edges);
    }

    private List<Station> getStations(List<Edge> edges) {
        if (edges.size() == 0) {
            return new ArrayList<>();
        }

        List<Station> stations = new ArrayList();
        Station lastStation = findFirstStation(edges);
        stations.add(lastStation);

        while (true) {
            Station nextStation = findNextStationOf(edges, lastStation);
            if (nextStation == null) {
                break;
            }
            stations.add(nextStation);
            lastStation = nextStation;
        }

        return stations;
    }

    private Station findFirstStation(List<Edge> edges) {
        List<Station> sourceStations = edges.stream()
                .map(it -> it.getTarget())
                .collect(Collectors.toList());

        return edges.stream()
                .map(it -> it.getSource())
                .filter(it -> !sourceStations.contains(it))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Station findNextStationOf(List<Edge> edges, Station firstStation) {
        return edges.stream()
                .filter(it -> firstStation.equals(it.getSource()))
                .map(it -> it.getTarget())
                .findFirst()
                .orElse(null);
    }

    public Edges removeStation(Station station) {
        List<Edge> replaceEdge = this.edges.stream()
                .filter(it -> it.hasStation(station))
                .collect(Collectors.toList());

        if (replaceEdge.size() == 0) {
            throw new RuntimeException();
        }

        List<Edge> newEdges = this.edges.stream()
                .filter(it -> !replaceEdge.contains(it))
                .collect(Collectors.toList());

        if (replaceEdge.size() == 1) {
            this.edges = newEdges;
            return new Edges(newEdges);
        }

        Edge newEdge = Edge.of(getSourceStationOf(station), getTargetStationOf(station));
        newEdges.add(newEdge);

        return new Edges(newEdges);
    }

    private Station getSourceStationOf(Station station) {
        return this.edges.stream()
                .filter(it -> station.equals(it.getTarget()))
                .map(it -> it.getSource())
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Station getTargetStationOf(Station station) {
        return this.edges.stream()
                .filter(it -> station.equals(it.getSource()))
                .map(it -> it.getTarget())
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Edges add(Edge edge) {
        List<Edge> newEdges = new ArrayList<>();
        newEdges = this.edges.stream().collect(Collectors.toList());
        newEdges.add(edge);
        return new Edges(newEdges);
    }

    private Integer sum(List<Edge> replaceEdge) {
        return replaceEdge.stream().map(it -> it.getDistance()).reduce(0, Integer::sum);
    }
}