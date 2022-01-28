package nextstep.subway.station.repository;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
}