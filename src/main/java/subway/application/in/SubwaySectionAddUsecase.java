package subway.application.in;

import lombok.Builder;
import subway.domain.Kilometer;
import subway.domain.Station;
import subway.domain.SubwayLine;

public interface SubwaySectionAddUsecase {

    void addSubwaySection(Command command);

    class Command {

        private SubwayLine.Id subwayLineId;
        private SubwaySection subwaySection;

        @Builder
        public Command(SubwayLine.Id subwayLineId, SubwaySection subwaySection) {
            this.subwayLineId = subwayLineId;
            this.subwaySection = subwaySection;
        }

        public SubwayLine.Id getSubwayLineId() {
            return subwayLineId;
        }

        public Station.Id getUpStationId() {
            return subwaySection.upStationId;
        }

        public Station.Id getDownStationId() {
            return subwaySection.downStationId;
        }

        public Kilometer getDistance() {
            return subwaySection.distance;
        }

        public static class SubwaySection {
            private Station.Id upStationId;
            private Station.Id downStationId;
            private Kilometer distance;

            public SubwaySection() {
            }

            @Builder
            public SubwaySection(Station.Id upStationId, Station.Id downStationId, Kilometer distance) {
                this.upStationId = upStationId;
                this.downStationId = downStationId;
                this.distance = distance;
            }
        }

    }

}
