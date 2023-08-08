package subway.application.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.command.in.SubwaySectionCloseUsecase;
import subway.application.command.out.StationLoadPort;
import subway.application.command.out.SubwayLineLoadPort;
import subway.application.command.out.SubwaySectionClosePort;
import subway.domain.SectionCloseManager;
import subway.domain.Station;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;

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
        SubwayLine subwayLine = getSubwayLineBy(command);
        Station station = getStationBy(command);

        subwayLine.closeSection(station, sectionCloseManager);

        subwaySectionClosePort.closeSection(subwayLine);
    }

    private Station getStationBy(Command command) {
        return stationLoadPort.findOne(command.getStationId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("%d는 존재하지 않는 역 id 입니다.", command.getStationId().getValue())));
    }

    private SubwayLine getSubwayLineBy(Command command) {
        return subwayLineLoadPort.findOne(command.getSubwayLineId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("%d는 존재하지 않는 노선 id 입니다.", command.getSubwayLineId().getValue())));
    }
}
