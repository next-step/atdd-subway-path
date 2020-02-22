package atdd.edge;

import atdd.line.Line;
import atdd.station.Station;

import java.util.Set;

public interface EdgeService {

    Edge create(Edge edge);

    void delete(Edge edge);

    Set<Line> findLinesByStation(Station station);

    Set<Station> findStationsByLine(Line line);
}
