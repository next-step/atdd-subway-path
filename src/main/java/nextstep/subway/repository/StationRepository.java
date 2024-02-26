package nextstep.subway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findStationByName(String name);
}