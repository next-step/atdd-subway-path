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
import java.util.Optional;

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
        Station upStation = getStationBy(command.getUpStationId(), idToStationMap);
        Station downStation = getStationBy(command.getDownStationId(), idToStationMap);

        SubwaySection subwaySection = SubwaySection.register(upStation, downStation, command.getDistance());

        SubwayLine subwayLine = SubwayLine.register(command.getName(), command.getColor(), subwaySection);

        return subwayLineRegisterPort.register(subwayLine);
    }

    private static Station getStationBy(Station.Id id, Map<Station.Id, Station> idToStationMap) {
        return Optional
                .ofNullable(idToStationMap.get(id))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("%d는 존재하지 않는 역입니다.", id.getValue())));
    }
}
