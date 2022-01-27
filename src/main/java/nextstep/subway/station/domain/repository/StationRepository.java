package nextstep.subway.station.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.station.domain.model.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
}
