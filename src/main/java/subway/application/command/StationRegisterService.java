package subway.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.command.in.StationRegisterUsecase;
import subway.application.command.out.StationRegisterPort;
import subway.application.response.StationResponse;
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
