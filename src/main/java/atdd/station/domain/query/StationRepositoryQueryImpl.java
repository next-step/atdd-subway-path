package atdd.station.domain.query;

import atdd.station.domain.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class StationRepositoryQueryImpl implements StationRepositoryQuery {

    private final EntityManager em;

    @Override
    public StationQueryView findStationWithLine(Long stationId) {
        final Station findStation = findStationById(stationId);

        if (Objects.isNull(findStation)) {
            return new StationQueryView();
        }

        final List<StationLineQueryView> lineQueryViews = findLines(findStation.getId());
        return new StationQueryView(findStation, lineQueryViews);
    }

    private Station findStationById(Long stationId) {
        final List<Station> stations = em.createQuery("select s from Station s where s.id = :id", Station.class)
                .setParameter("id", stationId)
                .getResultList();

        return CollectionUtils.isEmpty(stations) ? null : stations.get(0);
    }

    private List<StationLineQueryView> findLines(Long stationId) {
        return em.createQuery(
                "select distinct new atdd.station.domain.query.StationLineQueryView(l.id, l.name) " +
                        "from Line l " +
                        "left outer join Edge e on e.line = l " +
                        "where e.sourceStation.id = :id or e.targetStation.id = :id", StationLineQueryView.class)
                .setParameter("id", stationId)
                .getResultList();
    }

}
