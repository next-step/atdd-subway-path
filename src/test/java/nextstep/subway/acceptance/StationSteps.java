package nextstep.subway.acceptance;

import nextstep.subway.applicaion.dto.StationRequest;

import static nextstep.subway.utils.AcceptanceTestUtils.post;

public class StationSteps {

    public static Long 지하철역_생성_요청(String name) {
        return post("/stations", new StationRequest(name)).jsonPath().getLong("id");
    }
}
