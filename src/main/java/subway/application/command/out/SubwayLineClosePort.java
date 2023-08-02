package subway.application.command.out;

import subway.domain.SubwayLine;

public interface SubwayLineClosePort {

    void closeSubwayLine(SubwayLine.Id id);
}
