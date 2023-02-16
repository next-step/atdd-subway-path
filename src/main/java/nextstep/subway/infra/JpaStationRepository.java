package nextstep.subway.infra;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStationRepository extends JpaRepository<Station, Long>, StationRepository {

}
