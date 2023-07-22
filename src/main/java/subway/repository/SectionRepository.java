package subway.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import subway.domain.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByLineId(Long id);
}
