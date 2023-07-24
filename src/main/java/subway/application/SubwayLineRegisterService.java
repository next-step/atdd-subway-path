package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.out.SubwayLineRegisterPort;
import subway.application.in.SubwayLineRegisterUsecase;
import subway.application.out.StationMapLoadByInPort;
import subway.application.query.response.SubwayLineResponse;
import subway.domain.Station;
import subway.domain.SubwayLine;
import subway.domain.SubwaySection;

import java.util.List;
import java.util.Map;

@Service
@Transactional
class SubwayLineRegisterService implements SubwayLineRegisterUsecase {

    private final SubwayLineRegisterPort subwayLineRegisterPort;
    private final StationMapLoadByInPort stationMapLoadByInPort;

    public SubwayLineRegisterService(SubwayLineRegisterPort subwayLineRegisterPort, StationMapLoadByInPort stationMapLoadByInPort) {
        this.subwayLineRegisterPort = subwayLineRegisterPort;
        this.stationMapLoadByInPort = stationMapLoadByInPort;
    }

    @Override
    public SubwayLineResponse registerSubwayLine(SubwayLineRegisterUsecase.Command command) {
        Map<Station.Id, Station> idToStationMap = stationMapLoadByInPort.findAllByIn(List.of(command.getUpStationId(), command.getDownStationId()));
        Station upStation = idToStationMap.get(command.getUpStationId());
        Station downStation = idToStationMap.get(command.getDownStationId());

        SubwaySection subwaySection = SubwaySection.register(upStation, downStation, command.getDistance());

        SubwayLine subwayLine = SubwayLine.register(command.getName(), command.getColor(), subwaySection);

        return subwayLineRegisterPort.register(subwayLine);
    }
}
