package subway.application.query.out;

import subway.application.response.SubwayLineResponse;
import subway.domain.SubwayLine;

public interface SubwayLineDetailQueryPort {
    SubwayLineResponse findOne(SubwayLine.Id id);
}
