package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.station.Station;

public interface LineRepository extends JpaRepository<Line, Long> {
}
