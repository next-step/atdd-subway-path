package subway.application.command.in;

import subway.application.response.StationResponse;

public interface StationRegisterUsecase {
    StationResponse saveStation(Command command);

    class Command {
        private final String name;

        public Command(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
