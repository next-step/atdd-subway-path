package subway.application.command.in;

import subway.domain.SubwayLine;

public interface SubwayLineCloseUsecase {

    void closeSubwayLine(Command command);

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
