package nextstep.subway.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Section findByUpStation(Station upStation);
}
