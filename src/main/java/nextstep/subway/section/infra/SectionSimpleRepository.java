package nextstep.subway.section.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.section.domain.Section;

public interface SectionSimpleRepository extends SectionRepository, JpaRepository<Section, Long> {

}
