package subway.application.command.in;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.application.command.validator.SubwaySectionCloseCommandValidator;
import subway.domain.Station;
import subway.domain.SubwayLine;

public interface SubwaySectionCloseUsecase {

    void closeSection(Command command);

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Command {

        private SubwayLine.Id subwayLineId;

        private SectionCommand section;

        public Station.Id getStationId() {
            return section.getStationId();
        }

        public Command(SubwayLine.Id subwayLineId, SectionCommand section, SubwaySectionCloseCommandValidator validator) {
            this.subwayLineId = subwayLineId;
            this.section = section;
            validator.validate(this);
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SectionCommand {
            private Station.Id stationId;
            public SectionCommand(Station.Id stationId) {
                this.stationId = stationId;
            }
        }

    }

}
