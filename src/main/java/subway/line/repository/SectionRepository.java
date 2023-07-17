package subway.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.line.model.Section;
import subway.station.model.Station;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByUpStationOrDownStation(Station upStation, Station downStation);
}
