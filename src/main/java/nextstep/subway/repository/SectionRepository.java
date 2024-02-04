package nextstep.subway.repository;


import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findByLine(Line line);

	void deleteByLine_Id(Long lineId);

	Section findByLineAndDownStationId(Line line, Long stationId);
}
