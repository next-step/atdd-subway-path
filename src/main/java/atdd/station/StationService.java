package atdd.station;

import atdd.line.Line;

import java.util.List;
import java.util.Set;

public interface StationService {

    Station create(Station station);

    void delete(Station station);

    List<Station> findAll();

    Set<Station> findBy(Line line);

    Station findBy(Long id);
}
