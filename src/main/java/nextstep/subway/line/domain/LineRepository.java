package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {

    boolean existsByName(String name);
}
