package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.acceptance.test.utils.Lines;
import nextstep.subway.acceptance.test.utils.Paths;
import nextstep.subway.acceptance.test.utils.Stations;
import nextstep.subway.utils.ApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 검색")
public class PathTest extends AcceptanceTest {
    /**
     *                     (10)
     *      연신내역    --- *1호선* ---   서울역
     *        |                           |
     * (10) *2호선*                    *4호선* (10)
     *        |                           |
     *      삼성역     --- *3호선* --  -  강남역
     *                    (10)
     */
    /**
     * Given 지하철 노선 생성 및 노선 구간 생성을 하고,
     * When 출발역에서 도착역을 탐색하면,
     * Then 지나가는 역의 리스트와 거리가 표시되고 성공한다.
     * @see nextstep.subway.ui.PathController#getShortestDistanceAndPath
     */
    @DisplayName("최단거리 탐색 테스트")
    @Test
    void 최단거리_탐색() {
        //given
        ApiUtil.지하철역_생성_API(Stations.연신내역);
        ApiUtil.지하철역_생성_API(Stations.서울역);
        ApiUtil.지하철역_생성_API(Stations.강남역);
        ApiUtil.지하철역_생성_API(Stations.삼성역);

        ApiUtil.지하철_노선_생성_API(Lines.일호선_연신내역_서울역);
        ApiUtil.지하철_노선_생성_API(Lines.이호선_연신내역_삼성역);
        ApiUtil.지하철_노선_생성_API(Lines.삼호선_삼성역_강남역);
        ApiUtil.지하철_노선_생성_API(Lines.사호선_강남역_서울역);


        // when
        ExtractableResponse<Response> Response = ApiUtil.최단거리_탐색_API(Paths.최단거리_탐색);
        System.out.println(Response.body());
        System.out.println(Response.statusCode());

        // then
//        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @BeforeEach
    public void 초기화() {
        Lines.파람_초기화();
        Stations.파람_초기화();
        Paths.파람_초기화();
    }
}
