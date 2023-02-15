package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station station = stationRepository.save(new Station(stationRequest.getName()));
        return new StationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(final Long id) {
        final Station station = stationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STATION_NOT_FOUND));
        stationRepository.delete(station);
    }

    public Station findById(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STATION_NOT_FOUND));
    }
}
