package subway.db.h2.adapter;

import subway.core.common.PersistenceAdapter;
import subway.db.h2.mapper.StationMapper;
import subway.db.h2.repository.StationRepository;
import subway.db.h2.entity.StationJpa;
import subway.application.out.StationLoadPort;
import subway.domain.Station;

import java.util.NoSuchElementException;

@PersistenceAdapter
public class StationLoadPersistenceAdapter implements StationLoadPort {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public StationLoadPersistenceAdapter(StationRepository stationRepository, StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
    }


    @Override
    public Station findOne(Station.Id stationId) {
        StationJpa stationJpa = stationRepository.findById(stationId.getValue()).orElseThrow(() -> new NoSuchElementException("해당하는 지하철 역이 없습니다."));
        return stationMapper.from(stationJpa);
    }
}
