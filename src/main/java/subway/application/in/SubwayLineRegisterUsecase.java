package subway.application.in;

import subway.application.query.response.SubwayLineResponse;
import subway.domain.Kilometer;
import subway.domain.Station;

public interface SubwayLineRegisterUsecase {
    SubwayLineResponse registerSubwayLine(Command command);

    class Command {
        private final String name;
        private final String color;
        private final Station.Id upStationId;
        private final Station.Id downStationId;
        private final Kilometer distance;

        public Command(String name, String color, Station.Id upStationId, Station.Id downStationId, Kilometer distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public Station.Id getUpStationId() {
            return upStationId;
        }

        public Station.Id getDownStationId() {
            return downStationId;
        }

        public Kilometer getDistance() {
            return distance;
        }
    }
}
