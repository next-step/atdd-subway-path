package nextstep.subway.assertionsFixtures;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAssertions {
    public static void 노선_포함된_거리_검증(ExtractableResponse<Response> 특정_구간, Integer... 거리) {
        assertThat(특정_구간.jsonPath().getList("distance")).containsExactly(거리);
    }

    public static void 상행선_포함_검증(ExtractableResponse<Response> 특정_구간, String... 상행선) {
        assertThat(특정_구간.jsonPath().getList("upStation.name")).containsExactly(상행선);
    }

    public static void 하행선_포함_검증(ExtractableResponse<Response> 특정_구간, String... 하행선) {
        assertThat(특정_구간.jsonPath().getList("downStation.name")).containsExactly(하행선);
    }

    public static void 구간에_포함된_역_검증(ExtractableResponse<Response> 특정_구간, Long... 구간_아이디) {
        assertThat(특정_구간.jsonPath().getList("stations.id", Long.class)).containsExactly(구간_아이디);
    }
}
