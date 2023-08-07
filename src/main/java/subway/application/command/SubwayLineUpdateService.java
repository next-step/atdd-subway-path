package subway.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.command.in.SubwayLineUpdateUsecase;
import subway.application.command.out.SubwayLineLoadPort;
import subway.application.command.out.SubwayLineUpdatePort;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;

@Service
@Transactional
public class SubwayLineUpdateService implements SubwayLineUpdateUsecase {

    private final SubwayLineLoadPort subwayLineLoadPort;
    private final SubwayLineUpdatePort subwayLineUpdatePort;

    public SubwayLineUpdateService(SubwayLineLoadPort subwayLineLoadPort, SubwayLineUpdatePort subwayLineUpdatePort) {
        this.subwayLineLoadPort = subwayLineLoadPort;
        this.subwayLineUpdatePort = subwayLineUpdatePort;
    }

    @Override
    public void updateSubwayLine(Command command) {
        SubwayLine subwayLine = getSubwayLineBy(command);
        subwayLine.update(command.getName(), command.getColor());
        subwayLineUpdatePort.update(subwayLine);
    }

    private SubwayLine getSubwayLineBy(Command command) {
        return subwayLineLoadPort.findOne(command.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("%d는 존재하지 않는 노선 id 입니다.", command.getId().getValue())));
    }
}
