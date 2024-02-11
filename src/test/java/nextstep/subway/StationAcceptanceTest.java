package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.controller.dto.StationResponse;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static nextstep.subway.fixture.StationFixture.GANGNAM_STATION;
import static nextstep.subway.fixture.StationFixture.SEOLLEUNG_STATION;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String 강남역 = "강남역";
    private static final String 선릉역 = "선릉역";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철역_생성_요청(GANGNAM_STATION.toCreateRequest(), CREATED.value());

        // then
        ExtractableResponse<Response> findResponse = 지하철역_조회_요청(OK.value());
        List<String> stationsNames = findResponse.jsonPath().getList("name", String.class);
        assertThat(stationsNames).hasSize(1)
                .containsExactly(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void selectStation() {
        // given
        지하철역_생성_요청(GANGNAM_STATION.toCreateRequest(), CREATED.value());
        지하철역_생성_요청(SEOLLEUNG_STATION.toCreateRequest(), CREATED.value());

        // when
        ExtractableResponse<Response> findResponse = 지하철역_조회_요청(OK.value());
        List<String> stationsNames = findResponse.jsonPath().getList("name", String.class);

        // then
        assertThat(stationsNames).hasSize(2)
                .containsExactly("강남역", 선릉역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void removeStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(GANGNAM_STATION.toCreateRequest(), CREATED.value());
        StationResponse stationResponse = createResponse.as(StationResponse.class);

        // when
        지하철역_삭제_요청(stationResponse.getId(), NO_CONTENT.value());

        // then
        ExtractableResponse<Response> findResponse = 지하철역_조회_요청(OK.value());
        List<String> stationsNames = findResponse.jsonPath().getList("name", String.class);
        assertThat(stationsNames).isEmpty();
    }

    private ExtractableResponse<Response> 지하철역_조회_요청(int statusCode) {
        return get("/stations/all", OK.value(), new HashMap<>());
    }

    private ExtractableResponse<Response> 지하철역_삭제_요청(Long id, int statusCode) {
        return delete("/stations/{id}", statusCode, new HashMap<>(), id);
    }

}
