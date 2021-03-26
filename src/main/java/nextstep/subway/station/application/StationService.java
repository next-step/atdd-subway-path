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
        checkDuplicateStationName(loginMember, stationRequest);
        Station station = new Station(loginMember.getId(), stationRequest.getName());
        Station persistStation = stationRepository.save(station);
        return StationResponse.of(persistStation);
    }

    private void checkDuplicateStationName(LoginMember loginMember, StationRequest stationRequest) {
        List<Station> stations = stationRepository.findAllByUserId(loginMember.getId());
        stations.stream()
                .filter(it -> it.getName().equals(stationRequest.getName()))
                .findFirst()
                .ifPresent(it -> {
                    throw new StationDuplicateException();
                });
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations(LoginMember loginMember) {
        List<Station> stations = stationRepository.findAllByUserId(loginMember.getId());

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public void updateStation(LoginMember loginMember, Long id, StationRequest stationRequest) {
        checkDuplicateStationName(loginMember, stationRequest);
        Station station = findMyStationById(loginMember, id);
        station.update(new Station(loginMember.getId(), stationRequest.getName()));
    }

    public void deleteStationById(LoginMember loginMember, Long id) {
        Station station = findMyStationById(loginMember, id);
        stationRepository.delete(station);
    }

    public Station findMyStationById(LoginMember loginMember, Long id) {
        Station station = findStationById(id);
        checkOwner(loginMember, station);
        return station;
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
    }

    private void checkOwner(LoginMember loginMember, Station station) {
        if (!station.isOwner(loginMember.getId())) {
            throw new StationNotFoundException();
        }
    }
}
