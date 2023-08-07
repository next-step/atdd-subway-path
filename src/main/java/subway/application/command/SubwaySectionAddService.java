package subway.application.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.command.out.SubwaySectionAddPort;
import subway.application.command.in.SubwaySectionAddUsecase;
import subway.application.command.out.StationMapLoadByInPort;
import subway.application.command.out.SubwayLineLoadPort;
import subway.domain.SectionAddManager;
import subway.domain.Station;
import subway.domain.SubwayLine;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
class SubwaySectionAddService implements SubwaySectionAddUsecase {

    private final StationMapLoadByInPort stationMapLoadByInPort;
    private final SubwayLineLoadPort subwayLineLoadPort;
    private final SectionAddManager sectionAddManager;
    private final SubwaySectionAddPort subwaySectionAddPort;

    @Autowired
    SubwaySectionAddService(StationMapLoadByInPort stationMapLoadByInPort, SubwayLineLoadPort subwayLineLoadPort, SectionAddManager sectionAddManager, SubwaySectionAddPort subwaySectionAddPort) {
        this.stationMapLoadByInPort = stationMapLoadByInPort;
        this.subwayLineLoadPort = subwayLineLoadPort;
        this.sectionAddManager = sectionAddManager;
        this.subwaySectionAddPort = subwaySectionAddPort;
    }

    @Override
    public void addSubwaySection(Command command) {
        Command.SectionCommand commandSection = command.getSubwaySection();
        Map<Station.Id, Station> idToStationMap = getStationMapBy(commandSection);

        Station upStation = getStationBy(commandSection.getUpStationId(), idToStationMap);
        Station downStation = getStationBy(commandSection.getDownStationId(), idToStationMap);
        SubwayLine subwayLine = getSubwayLineBy(command);

        subwayLine.addSection(upStation, downStation, commandSection.getDistance(), sectionAddManager);
        subwaySectionAddPort.addSubwaySection(subwayLine);
    }

    private SubwayLine getSubwayLineBy(Command command) {
        return subwayLineLoadPort.findOne(command.getSubwayLineId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("%d는 존재하지 않는 노선 id 입니다.", command.getSubwayLineId().getValue())));
    }

    private Map<Station.Id, Station> getStationMapBy(Command.SectionCommand commandSection) {
        List<Station.Id> stationIds = List.of(commandSection.getUpStationId(), commandSection.getDownStationId());
        return stationMapLoadByInPort.findAllByIn(stationIds);
    }

    private static Station getStationBy(Station.Id id, Map<Station.Id, Station> idToStationMap) {
        return Optional
                .ofNullable(idToStationMap.get(id))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("%d는 존재하지 않는 역 id 입니다.", id.getValue())));
    }
}
