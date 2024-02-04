package nextstep.subway.station.service;

import nextstep.subway.station.repository.domain.Station;

public interface StationProvider {
    Station findById(Long id);
}
