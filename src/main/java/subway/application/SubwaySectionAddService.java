package subway.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.out.SubwaySectionAddPort;
import subway.application.in.SubwaySectionAddUsecase;
import subway.application.out.StationMapLoadByInPort;
import subway.application.out.SubwayLineLoadPort;
import subway.domain.SectionUpdateManager;
import subway.domain.Station;
import subway.domain.SubwayLine;
import subway.domain.SubwaySection;

import java.util.List;
import java.util.Map;

@Transactional
@Service
public class SubwaySectionAddService implements SubwaySectionAddUsecase {

    private final StationMapLoadByInPort stationMapLoadByInPort;
    private final SubwayLineLoadPort subwayLineLoadPort;
    private final SectionUpdateManager sectionUpdateManager;
    private final SubwaySectionAddPort subwaySectionAddPort;

    @Autowired
    public SubwaySectionAddService(StationMapLoadByInPort stationMapLoadByInPort, SubwayLineLoadPort subwayLineLoadPort, SectionUpdateManager sectionUpdateManager, SubwaySectionAddPort subwaySectionAddPort) {
        this.stationMapLoadByInPort = stationMapLoadByInPort;
        this.subwayLineLoadPort = subwayLineLoadPort;
        this.sectionUpdateManager = sectionUpdateManager;
        this.subwaySectionAddPort = subwaySectionAddPort;
    }

    @Override
    public void addSubwaySection(Command command) {
        Map<Station.Id, Station> idToStationMap = stationMapLoadByInPort.findAllByIn(List.of(command.getUpStationId(), command.getDownStationId()));
        Station upStation = idToStationMap.get(command.getUpStationId());
        Station downStation = idToStationMap.get(command.getDownStationId());

        SubwaySection subwaySection = SubwaySection.register(upStation, downStation, command.getDistance());
        SubwayLine subwayLine = subwayLineLoadPort.findOne(command.getSubwayLineId());
        subwayLine.updateSection(subwaySection, sectionUpdateManager);
        subwaySectionAddPort.addSubwaySection(subwayLine);
    }

}
