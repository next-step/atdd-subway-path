package nextstep.subway.station.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nextstep.subway.station.domain.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    boolean existsByName(String name);
}
