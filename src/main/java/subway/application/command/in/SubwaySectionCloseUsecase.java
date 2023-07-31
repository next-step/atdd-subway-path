package subway.application.command.in;

import subway.domain.Station;
import subway.domain.SubwayLine;

public interface SubwaySectionCloseUsecase {

    void closeSection(Command command);

    class Command {
        private SubwayLine.Id subwayLineId;

        private SubwaySection subwaySection;

        public SubwayLine.Id getSubwayLineId() {
            return subwayLineId;
        }

        public Station.Id getStationId() {
            return subwaySection.getStationId();
        }

        public Command(SubwayLine.Id subwayLineId, SubwaySection subwaySection) {
            this.subwayLineId = subwayLineId;
            this.subwaySection = subwaySection;
        }

        public static class SubwaySection {
            private Station.Id stationId;

            public SubwaySection(Station.Id stationId) {
                this.stationId = stationId;
            }

            public Station.Id getStationId() {
                return stationId;
            }
        }

    }

}
