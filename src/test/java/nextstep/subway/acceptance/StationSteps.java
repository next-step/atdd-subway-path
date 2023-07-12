package nextstep.subway.acceptance;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;

import static nextstep.subway.utils.AcceptanceTestUtils.post;

public class StationSteps {

    public static StationResponse 지하철역_생성_요청(String name) {
        return post("/stations", new StationRequest(name)).as(StationResponse.class);
    }
}
