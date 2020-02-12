package atdd.station;

import java.util.List;

public interface StationService {

    Station create(Station station);

    void delete(Station station);

    List<Station> findAll();
}
