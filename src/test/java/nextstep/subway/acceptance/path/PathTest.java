package nextstep.subway.acceptance.path;

import lombok.val;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.acceptance.test.utils.Lines;
import nextstep.subway.acceptance.test.utils.Paths;
import nextstep.subway.acceptance.test.utils.Stations;
import nextstep.subway.utils.ApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        val response = ApiUtil.최단거리_탐색(Paths.최단거리_탐색);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<HashMap<String, String>> stations = response.body().jsonPath().getList("stations");
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).get("name").equals("연신내")).isTrue();
        assertThat(stations.get(1).get("name").equals("서울역")).isTrue();
        assertThat(stations.get(2).get("name").equals("강남역")).isTrue();
    }

    @BeforeEach
    public void 초기화() {
        Lines.파람_초기화();
        Stations.파람_초기화();
        Paths.파람_초기화();
    }
}
