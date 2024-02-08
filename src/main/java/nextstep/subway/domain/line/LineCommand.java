package nextstep.subway.domain.line;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.interfaces.line.dto.LineRequest;

public class LineCommand {
    @Getter
    @RequiredArgsConstructor
    public static class SectionAddCommand {
        private final Long lineId;
        private final Long upStationId;
        private final Long downStationId;
        private final Long distance;

        public static SectionAddCommand of (Long lineId, LineRequest.Section request) {
            return new SectionAddCommand(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        }

    }

    @Getter
    @RequiredArgsConstructor
    public static class SectionDeleteCommand {
        private final Long lineId;
        private final Long stationId;

        public static SectionDeleteCommand of(Long lineId, Long stationId) {
            return new SectionDeleteCommand(lineId, stationId);
        }

    }
}
