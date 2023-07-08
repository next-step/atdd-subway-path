package nextstep.subway.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import subway.line.domain.Line;
import subway.section.domain.Section;
import subway.station.domain.Station;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    void deleteByLineAndDownStation(Line line, Station downStation);
}
