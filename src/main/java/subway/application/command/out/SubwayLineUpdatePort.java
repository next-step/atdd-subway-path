package subway.application.command.out;

import subway.domain.SubwayLine;

public interface SubwayLineUpdatePort {

    void update(SubwayLine subwayLine);
}
