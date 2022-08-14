package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return StationResponse.createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    /*
    서비스가 response 객체 생성의 책임까지 갖고 있어서 문제가 되는 것이다.
    그러면 서비스의 책임이 더 많아지고, 다른 객체에서 이를 모킹할 때 어려움이 생긴다.
    따라서 해당 메서드가 정말 이 서비스에 필요한지 생각해 보는 것이 좋다.
     */
//    public StationResponse createStationResponse(Station station) {
//        return new StationResponse(
//                station.getId(),
//                station.getName()
//        );
//    }

    public Station findById(Long id) {
        return stationRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
