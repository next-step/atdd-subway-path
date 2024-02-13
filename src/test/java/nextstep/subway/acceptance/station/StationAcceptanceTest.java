package nextstep.subway.acceptance.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.acceptance.util.RestAssuredUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    private static String 강남역 = "강남역";
    private static String 교대역 = "교대역";
    private static String 역삼역 = "역삼역";
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = RestAssuredUtil.생성_요청(StationFixture.createStationParams(강남역),"/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssuredUtil.조회_요청("/stations").jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void findStation() {
        //given
        RestAssuredUtil.생성_요청(StationFixture.createStationParams(강남역), "/stations");
        RestAssuredUtil.생성_요청(StationFixture.createStationParams(교대역), "/stations");


        //when
        List<String> responseList = RestAssuredUtil.조회_요청("/stations").jsonPath().get("name");

        //then
        assertThat(responseList).containsAnyOf(강남역, 교대역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void removeStation() {
        //given
        ExtractableResponse<Response> response = RestAssuredUtil.생성_요청(StationFixture.createStationParams(역삼역), "/stations");

        //when
        ExtractableResponse<Response> deleteResponse = RestAssuredUtil.삭제_요청("/stations/" + response.jsonPath().getLong("id"));
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<String> responseList = RestAssuredUtil.조회_요청("/stations").jsonPath().get("name");

        assertThat(responseList).doesNotContain(역삼역);
    }
}