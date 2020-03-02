package atdd.path.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Embeddable
public class Edges {
    @OneToMany(mappedBy = "line")
    private List<Edge> edges = new ArrayList<>();

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
                .map(Edge::getTargetStation)
                .collect(Collectors.toList());

        return edges.stream()
                .map(Edge::getSourceStation)
                .filter(it -> !sourceStations.contains(it))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Station findNextStationOf(List<Edge> edges, Station firstStation) {
        return edges.stream()
                .filter(it -> firstStation.equals(it.getSourceStation()))
                .map(Edge::getTargetStation)
                .findFirst()
                .orElse(null);
    }

    public void removeStation(Station station) {
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
            return;
        }

        Edge newEdge = Edge.updateBuilder()
                .sourceStation(getSourceStationOf(station))
                .targetStation(getTargetStationOf(station))
                .distance(sum(replaceEdge))
                .build();
        newEdges.add(newEdge);

        this.edges = newEdges;
    }

    private Integer sum(List<Edge> replaceEdge) {
        return replaceEdge.stream().map(Edge::getDistance).reduce(0, Integer::sum);
    }

    private Station getSourceStationOf(Station station) {
        return this.edges.stream()
                .filter(it -> station.equals(it.getTargetStation()))
                .map(Edge::getSourceStation)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Station getTargetStationOf(Station station) {
        return this.edges.stream()
                .filter(it -> station.equals(it.getSourceStation()))
                .map(Edge::getTargetStation)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Edges add(Edge edge) {
        List<Edge> newEdges = new ArrayList<>(this.edges);
        newEdges.add(edge);
        return new Edges(newEdges);
    }
}
