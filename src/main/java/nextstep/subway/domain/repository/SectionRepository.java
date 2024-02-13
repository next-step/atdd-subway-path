package nextstep.subway.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
