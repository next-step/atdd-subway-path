package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.mapper.StationResponseMapper;
import subway.db.h2.mapper.StationJpaMapper;
import subway.db.h2.repository.StationRepository;
import subway.db.h2.entity.StationJpa;
import subway.application.command.out.StationRegisterPort;
import subway.application.response.StationResponse;
import subway.domain.Station;

@PersistenceAdapter
public class StationRegisterPersistenceAdapter implements StationRegisterPort {

    private final StationRepository stationRepository;
    private final StationJpaMapper stationJpaMapper;
    private final StationResponseMapper stationResponseMapper;

    public StationRegisterPersistenceAdapter(StationRepository stationRepository, StationJpaMapper stationJpaMapper, StationResponseMapper stationResponseMapper) {
        this.stationRepository = stationRepository;
        this.stationJpaMapper = stationJpaMapper;
        this.stationResponseMapper = stationResponseMapper;
    }

    @Override
    public StationResponse save(Station station) {
        StationJpa stationJpa = stationJpaMapper.from(station);
        stationRepository.save(stationJpa);

        return stationResponseMapper.from(stationJpa);
    }
}
