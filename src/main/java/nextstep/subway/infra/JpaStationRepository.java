package nextstep.subway.infra;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaStationRepository extends JpaRepository<Station, Long>, StationRepository {
    @Override
    Optional<Station> findById(Long aLong);

    @Override
    List<Station> findAll();

    @Override
    void deleteById(Long aLong);

    @Override
    <S extends Station> S save(S entity);
}
