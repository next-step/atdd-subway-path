package nextstep.subway.acceptance.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.val;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.acceptance.test.utils.Stations;
import nextstep.subway.utils.ApiUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = ApiUtil.지하철역_생성(Stations.강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 같은 이름으로 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 실패한다.
     * @see nextstep.subway.ui.StationController#createStation
     */
    @DisplayName("지하철역 이름 중복 생성 방지 테스트")
    @Test
    void 지하철역_이름_중복_생성_방지_테스트() {
        // given
        ApiUtil.지하철역_생성(Stations.강남역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철역_생성(Stations.강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        /// given
        ApiUtil.지하철역_생성(Stations.강남역);
        ApiUtil.지하철역_생성(Stations.역삼역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철역_전체_리스트_조회();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains("강남역", "역삼역");
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = ApiUtil.지하철역_생성(Stations.강남역);

        // when
        String uri = createResponse.header("Location");
        val response = ApiUtil.지하철역_삭제(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @BeforeAll
    public static void 초기화() {
        Stations.파람_초기화();
    }
}
