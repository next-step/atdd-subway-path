package nextstep.subway.repository;

import java.util.List;
import java.util.Optional;
import nextstep.subway.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<List<Section>> findAllByLineId(long stationLineId);

}
