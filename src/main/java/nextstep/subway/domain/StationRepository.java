package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    public List<Station> findByIdIn(List<Long> stationIds);
}
