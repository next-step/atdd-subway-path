package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.repository.StationRepository;
import subway.db.h2.entity.StationJpa;
import subway.db.h2.mapper.StationMapper;
import subway.application.out.StationMapLoadByInPort;
import subway.domain.Station;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@PersistenceAdapter
public class StationMapLoadByInPersistenceAdapter implements StationMapLoadByInPort {

    private final StationRepository stationRepository;

    private final StationMapper stationMapper;


    public StationMapLoadByInPersistenceAdapter(StationRepository stationRepository, StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
    }

    @Override
    public Map<Station.Id, Station> findAllByIn(List<Station.Id> stationIds) {
        List<Long> ids = createIdValues(stationIds);
        List<StationJpa> stationJpas = stationRepository.findAllByIdIn(ids);

        Map<Long, StationJpa> stationJpaMap = createLongToStationJpaMap(stationJpas);

        return createIdToStationMap(stationIds, stationJpaMap);
    }

    private Map<Station.Id, Station> createIdToStationMap(List<Station.Id> stationIds, Map<Long, StationJpa> stationJpaMap) {
        return stationIds.stream()
                .map(stationId -> stationMapper.from(stationJpaMap.get(stationId.getValue())))
                .collect(Collectors.toMap(Station::getId, Function.identity()));
    }

    private static Map<Long, StationJpa> createLongToStationJpaMap(List<StationJpa> stationJpas) {
        return stationJpas.stream().collect(Collectors.toMap(StationJpa::getId, Function.identity()));
    }

    private static List<Long> createIdValues(List<Station.Id> stationIds) {
        return stationIds.stream().map(Station.Id::getValue).collect(Collectors.toList());
    }

}
