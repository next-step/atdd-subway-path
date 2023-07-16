package subway.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.line.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
