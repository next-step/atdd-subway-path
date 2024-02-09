package nextstep.subway.station.service;

import nextstep.subway.station.domain.Station;

public interface StationProvider {
    Station findById(Long id);
}
