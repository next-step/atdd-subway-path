package nextstep.subway.station.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.dto.request.SaveStationRequest;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {
    private final StationJpaAdapter stationJpaAdapter;

    @Transactional
    public StationResponse saveStation(SaveStationRequest stationRequest) {
        Station station = stationJpaAdapter.save(stationRequest.toEntity());
        return StationResponse.of(station);
    }

    public List<StationResponse> findAllStations() {
        return stationJpaAdapter.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationJpaAdapter.deleteById(id);
    }
}
