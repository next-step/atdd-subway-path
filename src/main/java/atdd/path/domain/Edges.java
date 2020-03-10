package atdd.path.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Edges {
    @OneToMany(mappedBy = "line")
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
        return getStationsOfEdges();
    }

    private List<Station> getStationsOfEdges() {
        Station firstStation = findFirstStation();
        List<Station> stations = new ArrayList<>();
        stations.add(firstStation);
        Optional<Station> nextStation
                = findTargetStation(firstStation);

        while (nextStation.isPresent()) {
            stations.add(nextStation.get());
            nextStation = findTargetStation(nextStation.get());
        }
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

    public Optional<Station> findSourceStation(Station targetStation) {
        Optional<Station> sourceStation = edges.stream()
                .filter(it -> it.getTarget().equals(targetStation))
                .findFirst()
                .map(Edge::getSource);
        return sourceStation;
    }

    public Station findLastStation() {
        Station firstStation = this.findFirstStation();
        Optional<Station> targetStation = findTargetStation(firstStation);
        Station newSource = targetStation
                .orElseThrow(() -> new NoSuchElementException(firstStation.getName() + "의 타깃 지하철역이 없습니다."));

        while (targetStation.isPresent()) {
            newSource =targetStation.get();
            targetStation = findTargetStation(targetStation.get());
        }
        return newSource;
    }

    public Edges findEdgesAfterRemovalOfStation(Station stationToDelete) {
        List<Edge> collect = edges.stream()
                .filter(it -> !it.getTarget().equals(stationToDelete))
                .filter(it -> !it.getSource().equals(stationToDelete))
                .collect(Collectors.toList());
        return new Edges(collect);
    }

    public List<Long> findIdOfEdgesToDelete(Station stationToDelete) {
        List<Long> idToDelete = edges.stream()
                .filter(it -> it.getSource().equals(stationToDelete))
                .map(Edge::getId)
                .collect(Collectors.toList());

        edges.stream()
                .filter(it -> it.getTarget().equals(stationToDelete))
                .map(Edge::getId)
                .forEach(it -> idToDelete.add(it));

        return idToDelete;
    }
}
