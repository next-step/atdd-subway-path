package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<Section, Long> {
}
