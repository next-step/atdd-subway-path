package subway.application.command.out;

import subway.application.response.StationResponse;
import subway.domain.Station;

public interface StationRegisterPort {
    StationResponse save(Station station);
}
