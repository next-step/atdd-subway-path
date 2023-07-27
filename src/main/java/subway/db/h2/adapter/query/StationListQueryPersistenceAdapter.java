package subway.db.h2.adapter.query;

import subway.db.h2.mapper.StationResponseMapper;
import subway.db.h2.entity.StationJpa;
import subway.db.common.PersistenceAdapter;
import subway.db.h2.repository.StationRepository;
import subway.application.query.out.StationListQueryPort;
import subway.application.query.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@PersistenceAdapter
public class StationListQueryPersistenceAdapter implements StationListQueryPort {

    private final StationRepository stationRepository;
    private final StationResponseMapper stationResponseMapper;

    public StationListQueryPersistenceAdapter(StationRepository stationRepository, StationResponseMapper stationResponseMapper) {
        this.stationRepository = stationRepository;
        this.stationResponseMapper = stationResponseMapper;
    }

    @Override
    public List<StationResponse> findAll() {
        List<StationJpa> stationJpas = stationRepository.findAll();
        return stationJpas.stream().map(stationResponseMapper::from).collect(Collectors.toList());
    }
}
