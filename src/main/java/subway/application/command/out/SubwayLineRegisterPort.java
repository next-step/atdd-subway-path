package subway.application.command.out;

import subway.application.response.SubwayLineResponse;
import subway.domain.SubwayLine;

public interface SubwayLineRegisterPort {
    SubwayLineResponse register(SubwayLine subwayLine);
}
