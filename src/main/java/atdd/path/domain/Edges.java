package atdd.path.domain;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Edges {
    private List<Edge> edges = new ArrayList<>();

    public Edges() {
    }

    public Edges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Station> getStations() {
        return getStations(this.edges);
    }

    private List<Station> getStations(List<Edge> edges) {
        return null;
    }

    public Station findFirstStation() {
        List<Station> targets = edges.stream()
                .map(Edge::getTarget)
                .collect(Collectors.toList());

        Station firstStation = this.edges.stream()
                .map(Edge::getSource)
                .filter(it -> !targets.contains(it))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        return firstStation;
    }

    public Station findTargetStation(Station sourceStation) {
        Station nextStation = edges.stream()
                .filter(it -> it.getSource().equals(sourceStation))
                .findFirst()
                .map(Edge::getTarget)
                .orElseThrow(RuntimeException::new);

        return nextStation;
    }

    public Station findSourceStation(Station targetStation) {
        Station sourceStation = edges.stream()
                .filter(it -> it.getTarget().equals(targetStation))
                .findFirst()
                .map(Edge::getSource)
                .orElseThrow(RuntimeException::new);
        return sourceStation;
    }

    public Station findLastStation() {
        Station firstStation = this.findFirstStation();
        Station targetStation = findTargetStation(firstStation);
        Station newSource = targetStation;
        while (targetStation != null) {
            newSource = targetStation;
            targetStation = findTargetStation(targetStation);
        }
        return newSource;
    }
}
