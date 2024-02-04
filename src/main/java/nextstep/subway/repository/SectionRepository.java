package nextstep.subway.repository;


import nextstep.subway.domain.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Section findByLineIdAndId(Long lineId, Long sectionId);
}
