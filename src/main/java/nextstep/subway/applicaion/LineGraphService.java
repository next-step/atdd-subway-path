package nextstep.subway.applicaion;

import java.util.List;

import nextstep.subway.domain.line.LineSections;
import nextstep.subway.domain.station.Station;

public interface LineGraphService {

    List<Station> orderedStations(final LineSections sections);
}
