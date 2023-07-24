package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.in.StationRegisterUsecase;
import subway.application.out.StationRegisterPort;
import subway.application.query.response.StationResponse;
import subway.domain.Station;

@Service
@Transactional
class StationRegisterService implements StationRegisterUsecase {
    private final StationRegisterPort stationRegisterPort;

    public StationRegisterService(StationRegisterPort stationRegisterPort) {
        this.stationRegisterPort = stationRegisterPort;
    }

    @Override
    public StationResponse saveStation(Command command) {

        Station station = Station.register(command.getName());
        return stationRegisterPort.save(station);
    }
}
