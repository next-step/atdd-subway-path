package atdd.path.domain;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Station firstStation = findFirstStation();
        Station lastStation = findLastStation();
        Station prevLastStation = findSourceStation(lastStation);

        List<Station> stations = new ArrayList<>();
        stations.add(firstStation);
        Station nextStation = findTargetStation(firstStation).orElseThrow(RuntimeException::new);
        while(!nextStation.equals(prevLastStation)){
            stations.add(nextStation);
            nextStation = findTargetStation(nextStation).orElseThrow(RuntimeException::new);
        }
        stations.add(prevLastStation);
        stations.add(lastStation);
        return stations;
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

    public Optional<Station> findTargetStation(Station sourceStation) {
        Optional<Station> nextStation = edges.stream()
                .filter(it -> it.getSource().equals(sourceStation))
                .findFirst()
                .map(Edge::getTarget);

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
        Optional<Station> targetStation = findTargetStation(firstStation);
        Optional<Station> newSource = targetStation;

        while (targetStation.isPresent()) {
            newSource = targetStation;
            targetStation = findTargetStation(targetStation.get());
        }
        return newSource.get();
    }

    public void removeStation(Station testStation) {


    }
}
