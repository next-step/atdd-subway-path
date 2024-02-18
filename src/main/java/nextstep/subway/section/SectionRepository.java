package nextstep.subway.section;

import nextstep.subway.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Section findByUpStation(Station station);

    Section findByDownStation(Station station);
}
