package nextstep.subway.api.domain.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import nextstep.subway.api.domain.dto.inport.StationCreateCommand;
import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.infrastructure.persistence.StationRepository;
import nextstep.subway.api.interfaces.dto.response.StationResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    @Transactional
    public StationResponse saveStation(StationCreateCommand stationCreateRequest) {
        Station station = stationRepository.save(new Station(stationCreateRequest.getName()));
        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
