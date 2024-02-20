package nextstep.subway.domain.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import nextstep.subway.domain.line.domain.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Modifying
    @Query("DELETE FROM Section s WHERE s.downStation.id = :downStationId")
    void deleteByDownStationId(@Param("downStationId") Long downStationId);
}
