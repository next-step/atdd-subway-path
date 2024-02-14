package nextstep.subway.section;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Section findByUpStationId(long upStationsId);

    Section findByDownStationId(long downStationId);

    List<Section> findAllByLineId(Long lineId);

}
