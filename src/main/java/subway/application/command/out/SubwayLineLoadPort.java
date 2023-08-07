package subway.application.command.out;

import subway.domain.SubwayLine;

import java.util.Optional;

public interface SubwayLineLoadPort {
    Optional<SubwayLine> findOne(SubwayLine.Id id);
}
