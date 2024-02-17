package nextstep.subway.line.repository;

import nextstep.subway.line.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByDownStationId(Long downStationId);

}
