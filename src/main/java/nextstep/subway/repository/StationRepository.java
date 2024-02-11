package nextstep.subway.repository;

import nextstep.subway.exception.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.Station;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByIdIn(List<Long> ids);
    default Station getBy(Long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(
                        "지하철역이 존재하지 않습니다. id=" + id));
    }

}
