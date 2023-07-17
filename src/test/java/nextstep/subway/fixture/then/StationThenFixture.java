package nextstep.subway.fixture.then;

import static nextstep.subway.fixture.when.StationApiFixture.지하철역_리스트_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ListAssert;
import nextstep.subway.service.response.StationResponse;

public abstract class StationThenFixture {

    public static ListAssert<String> 지하철역_목록_조회시_생성한역을_포함하는지_검사(String stationName) {
        List<String> stationNames = 지하철역_리스트_조회()
            .jsonPath()
            .getList("name", String.class);

        return assertThat(stationNames).containsAnyOf(stationName);
    }

    public static AbstractStringAssert<?> 지하철역_목록_조회_지하철역_이름_검사(
        ExtractableResponse<Response> 응답결과, String stationName, int index) {
        return assertThat(응답결과.jsonPath().getString("[" + index+ "].name")).isEqualTo(stationName);
    }

    public static ListAssert<StationResponse> 지하철역_목록_리스트_크기_검사(
        ExtractableResponse<Response> response, int size) {
        return assertThat(response.jsonPath().getList("", StationResponse.class)).hasSize(size);
    }

    public static void 지하철역_목록_조회시_아무런값도_조회되지않음_검사() {
        assertThat(지하철역_리스트_조회().jsonPath().getList("")).isEmpty();
    }
}
