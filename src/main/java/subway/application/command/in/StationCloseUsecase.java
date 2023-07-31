package subway.application.command.in;

import subway.domain.Station;

public interface StationCloseUsecase {
    void closeStation(Command command);

    class Command {
        private final Station.Id id;

        public Command(Station.Id id) {
            this.id = id;
        }

        public Station.Id getId() {
            return id;
        }
    }
}
