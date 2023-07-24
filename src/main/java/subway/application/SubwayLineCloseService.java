package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.out.SubwayLineClosePort;
import subway.application.in.SubwayLineCloseUsecase;

@Service
@Transactional
class SubwayLineCloseService implements SubwayLineCloseUsecase {

    private final SubwayLineClosePort subwayLineClosePort;

    SubwayLineCloseService(SubwayLineClosePort subwayLineClosePort) {
        this.subwayLineClosePort = subwayLineClosePort;
    }

    @Override
    public void closeSubwayLine(Command command) {
        subwayLineClosePort.closeSubwayLine(command.getId());
    }
}
