package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineSectionRepository extends JpaRepository<LineSection, Long> {

  @Query("SELECT ls FROM LineSection ls JOIN ls.line ON ls.line.id = :lineId")
  List<LineSection> findAllByLineId(Long lineId);

  @Modifying
  @Query("DELETE FROM LineSection ls WHERE ls.downStation.id = :stationId")
  void deleteByLineIdAndStationId(Long stationId);
}
