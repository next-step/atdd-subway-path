package subway.application.command.out;

import subway.domain.SubwayLine;

public interface SubwayLineLoadPort {
    SubwayLine findOne(SubwayLine.Id id);
}
