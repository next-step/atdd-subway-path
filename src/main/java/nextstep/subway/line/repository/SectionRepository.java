package nextstep.subway.line.repository;

import nextstep.subway.line.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
