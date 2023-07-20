package nextstep.subway.fixture.acceptance.given;

import java.util.HashMap;
import java.util.Map;

public abstract class StationRequestFixture {

    public static Map<String, String> 지하철역_등록_요청_데이터_생성(String name) {

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return params;
    }
}
