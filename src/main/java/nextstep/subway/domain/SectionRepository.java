package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);
}
