package nextstep.subway.section;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

	Section findByDownStation_Id(Long downStationId);
}
