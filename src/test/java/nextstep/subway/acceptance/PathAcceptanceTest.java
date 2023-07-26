package nextstep.subway.acceptance;

import static nextstep.subway.fixture.acceptance.then.PathThenFixture.최적경로_가중치_확인;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.최적경로_도출순서_확인;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청_후_id_추출;
import static nextstep.subway.fixture.acceptance.when.PathApiFixture.경로조회_요청;
import static nextstep.subway.fixture.acceptance.when.SectionApiFixture.지하철_노선_구간_추가_등록;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_생성_요청_후_id_추출;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 조회 관련 기능")
public class PathAcceptanceTest extends AcceptanceTestConfig{


    @DisplayName("경로 조회")
    @Test
    void getPaths() {

        //given
        long 부평역 = 지하철역_생성_요청_후_id_추출("부평역");
        long 구로역 = 지하철역_생성_요청_후_id_추출("구로역");
        long 신도림역 = 지하철역_생성_요청_후_id_추출("신도림역");
        long 영등포구청역 = 지하철역_생성_요청_후_id_추출("영등포구청역");
        long 특급역 = 지하철역_생성_요청_후_id_추출("특급역");

        long 일호선 = 지하철역_노선_등록_요청_후_id_추출("일호선", "blue", 부평역, 구로역, 5);
        long 이호선 = 지하철역_노선_등록_요청_후_id_추출("이호선", "red", 신도림역, 영등포구청역, 10);
        long 특급선 = 지하철역_노선_등록_요청_후_id_추출("특급선", "green", 구로역, 특급역, 5);

        // 부평 - 5 - 구로 - 5 - 신도림 *** 10 *** 영등포구청
        //            | --- 5 --- 특급역 --- 5 --- |

        지하철_노선_구간_추가_등록(일호선, 신도림역, 구로역, 5);
        지하철_노선_구간_추가_등록(특급선, 영등포구청역, 특급역, 5);

        //when
        ExtractableResponse<Response> 경로조회_결과 = 경로조회_요청(부평역, 영등포구청역);

        //then
        assertAll(
            최적경로_도출순서_확인(경로조회_결과, 부평역, 구로역, 특급역, 영등포구청역),
            최적경로_가중치_확인(경로조회_결과, 15)
        );

    }

}
