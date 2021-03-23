package nextstep.subway.station.application;

import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(LoginMember loginMember, StationRequest stationRequest) {
        Station station = new Station(loginMember.getId(), stationRequest.getName());
        Station persistStation = stationRepository.save(station);
        return StationResponse.of(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations(LoginMember loginMember) {
        List<Station> stations = stationRepository.findAllByUserId(loginMember.getId());

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public void updateStation(LoginMember loginMember, Long id, StationRequest stationRequest) {
        Station persistStation = stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
        if (!persistStation.isOwner(loginMember.getId())) {
            throw new StationNotFoundException();
        }
        persistStation.update(new Station(loginMember.getId(), stationRequest.getName()));
    }

    public void deleteStationById(LoginMember loginMember, Long id) {
        Station persistStation = stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
        if (!persistStation.isOwner(loginMember.getId())) {
            throw new StationNotFoundException();
        }
        stationRepository.deleteById(id);
    }

    public Station findStationById(LoginMember loginMember, Long id) {
        Station persistStation = stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
        if (!persistStation.isOwner(loginMember.getId())) {
            throw new StationNotFoundException();
        }
        return persistStation;
    }
}
