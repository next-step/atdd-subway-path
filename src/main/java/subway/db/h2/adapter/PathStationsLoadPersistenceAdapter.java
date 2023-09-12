package subway.db.h2.adapter;

import lombok.RequiredArgsConstructor;
import subway.application.query.out.PathStationsLoadPort;
import subway.db.common.PersistenceAdapter;
import subway.db.h2.entity.StationJpa;
import subway.db.h2.mapper.PathStationMapper;
import subway.db.h2.repository.StationRepository;
import subway.domain.PathStation;

import java.util.List;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class PathStationsLoadPersistenceAdapter implements PathStationsLoadPort {

    private final StationRepository stationRepository;
    private final PathStationMapper pathStationMapper;

    @Override
    public Response findAllBy(List<PathStation.Id> pathStationIds) {
        List<Long> ids = pathStationIds.stream().map(PathStation.Id::getValue).collect(Collectors.toList());
        List<StationJpa> stationJpas = stationRepository.findAllByIdIn(ids);
        List<PathStation> pathStations = stationJpas.stream().map(pathStationMapper::from).collect(Collectors.toList());

        return Response.from(pathStations);
    }
}
