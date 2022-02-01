package nextstep.subway.applicaion.command;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.applicaion.query.StationQueryService;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.station.DuplicateStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationCommandService {

    private final StationRepository stationRepository;
    private final StationQueryService stationQueryService;

    public StationCommandService(StationRepository stationRepository,
                                 StationQueryService stationQueryService) {
        this.stationRepository = stationRepository;
        this.stationQueryService = stationQueryService;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        boolean existsStationByName = stationRepository.existsByName(stationRequest.getName());
        if (existsStationByName) {
            throw new DuplicateStationException(stationRequest.getName());
        }

        Station station = stationRepository.save(Station.of(stationRequest.getName()));
        return stationQueryService.createStationResponse(station);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

}
