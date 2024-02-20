package nextstep.subway.domain.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.station.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
    default Station getById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 지하철이 존재하지 않습니다."));
    }
}