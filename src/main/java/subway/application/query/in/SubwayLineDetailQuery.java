package subway.application.query.in;

import subway.application.response.SubwayLineResponse;
import subway.domain.SubwayLine;

public interface SubwayLineDetailQuery {

    SubwayLineResponse findOne(Command command);

    class Command {
        private final SubwayLine.Id id;

        public Command(SubwayLine.Id id) {
            this.id = id;
        }

        public SubwayLine.Id getId() {
            return id;
        }
    }
}
