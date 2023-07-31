package nextstep.subway.domain.delete;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

public interface SectionDeleteStrategy {

    boolean match(Stations stations, Station station);

    void delete(Sections sections, Station station);
}
