package nextstep.subway.station.adapters.persistence;

import lombok.RequiredArgsConstructor;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationJpaAdapter {

    private final StationRepository stationRepository;

    @Transactional
    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NotEntityFoundException(ErrorCode.NOT_EXIST_STATION));
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }

}
