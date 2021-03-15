package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<String> findByName(String name);

}
