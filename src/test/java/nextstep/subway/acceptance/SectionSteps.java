package nextstep.subway.acceptance;

import nextstep.subway.applicaion.dto.SectionRequest;

public class SectionSteps {

    public static SectionRequest createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }
}
