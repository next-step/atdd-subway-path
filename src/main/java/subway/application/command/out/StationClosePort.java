package subway.application.command.out;

import subway.domain.Station;

public interface StationClosePort {
    void closeStation(Station.Id id);
}
