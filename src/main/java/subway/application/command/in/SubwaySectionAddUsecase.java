package subway.application.command.in;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.application.command.validator.SubwaySectionAddCommandValidator;
import subway.domain.Kilometer;
import subway.domain.Station;
import subway.domain.SubwayLine;

public interface SubwaySectionAddUsecase {

    void addSubwaySection(Command command);

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Command {

        private SubwayLine.Id subwayLineId;
        private SectionCommand subwaySection;

        @Builder
        private Command(SubwayLine.Id subwayLineId, SectionCommand subwaySection, SubwaySectionAddCommandValidator validator) {
            this.subwayLineId = subwayLineId;
            this.subwaySection = subwaySection;
            validator.validate(this);
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SectionCommand {
            private Station.Id upStationId;
            private Station.Id downStationId;
            private Kilometer distance;

            @Builder
            private SectionCommand(Station.Id upStationId, Station.Id downStationId, Kilometer distance) {
                this.upStationId = upStationId;
                this.downStationId = downStationId;
                this.distance = distance;
            }
        }

    }

}
