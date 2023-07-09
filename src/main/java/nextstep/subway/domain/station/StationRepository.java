package nextstep.subway.domain.station;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.domain.station.exception.NoSuchStationException;

public interface StationRepository extends JpaRepository<Station, Long> {

    default Station getById(final Long id) throws NoSuchStationException {
        return findById(id).orElseThrow(() -> NoSuchStationException.from(id));
    }
}
