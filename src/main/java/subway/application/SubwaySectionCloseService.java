package subway.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.in.SubwaySectionCloseUsecase;
import subway.application.out.StationLoadPort;
import subway.application.out.SubwayLineLoadPort;
import subway.application.out.SubwaySectionClosePort;
import subway.domain.SectionCloseManager;
import subway.domain.Station;
import subway.domain.SubwayLine;

@Transactional
@Service
public class SubwaySectionCloseService implements SubwaySectionCloseUsecase {

    private final SectionCloseManager sectionCloseManager;
    private final SubwayLineLoadPort subwayLineLoadPort;
    private final StationLoadPort stationLoadPort;
    private final SubwaySectionClosePort subwaySectionClosePort;

    @Autowired
    public SubwaySectionCloseService(SectionCloseManager sectionCloseManager, SubwayLineLoadPort subwayLineLoadPort, StationLoadPort stationLoadPort, SubwaySectionClosePort subwaySectionClosePort) {
        this.sectionCloseManager = sectionCloseManager;
        this.subwayLineLoadPort = subwayLineLoadPort;
        this.stationLoadPort = stationLoadPort;
        this.subwaySectionClosePort = subwaySectionClosePort;
    }

    @Override
    public void closeSection(Command command) {
        SubwayLine subwayLine = subwayLineLoadPort.findOne(command.getSubwayLineId());
        Station station = stationLoadPort.findOne(command.getStationId());

        subwayLine.closeSection(station, sectionCloseManager);

        subwaySectionClosePort.closeSection(subwayLine);
    }
}
