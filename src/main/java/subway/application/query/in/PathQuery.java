package subway.application.query.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.application.query.validator.PathQueryCommandValidator;
import subway.application.response.PathResponse;
import subway.domain.PathStation;

public interface PathQuery {

    PathResponse findOne(PathQuery.Command command);

    @Getter
    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class Command {
        private PathStation.Id startStationId;
        private PathStation.Id endStationId;

        @Builder
        private Command(PathStation.Id startStationId, PathStation.Id endStationId, PathQueryCommandValidator validator) {
            this.startStationId = startStationId;
            this.endStationId = endStationId;
            validator.validate(this);
        }
    }
}
