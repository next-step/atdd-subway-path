package nextstep.subway.acceptance;

import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_잘못된요청_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.최적경로_가중치_확인;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.최적경로_도출순서_확인;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.출발지또는_목적지가_이어져있지않을때_에러메세지_검사;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.출발지또는_목적지가_존재하지않을때_에러메세지_검사;
import static nextstep.subway.fixture.acceptance.then.PathThenFixture.출발지와_목적지가_같은역일때_에러메세지_검사;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청_후_id_추출;
import static nextstep.subway.fixture.acceptance.when.PathApiFixture.경로조회_요청;
import static nextstep.subway.fixture.acceptance.when.SectionApiFixture.지하철_노선_구간_추가_등록;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_생성_요청_후_id_추출;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @DisplayName("출발지와 목적지가 같은 역일 수 없다.")
    @Test
    void getPaths_dontEqualsScoreAndTarget() {

        //given
        long 부평역 = 지하철역_생성_요청_후_id_추출("부평역");

        //when
        ExtractableResponse<Response> 경로조회_결과 = 경로조회_요청(부평역, 부평역);

        //then
        API_잘못된요청_응답코드_검사(경로조회_결과);
        출발지와_목적지가_같은역일때_에러메세지_검사(경로조회_결과);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("출발지와 목적지가 존재하지 않는역일 수 없다.")
    void getPaths_notExistStation(long source, long target) {

        지하철역_생성_요청_후_id_추출("부평역");

        //when
        ExtractableResponse<Response> 경로조회_결과 = 경로조회_요청(source, target);

        //then
        API_잘못된요청_응답코드_검사(경로조회_결과);
        출발지또는_목적지가_존재하지않을때_에러메세지_검사(경로조회_결과, -1);
    }

    static Stream<Arguments> getPaths_notExistStation() {

        return Stream.of(
            Arguments.of(1, -1),
            Arguments.of(-1, 1)
        );
    }
    @DisplayName("출발지와 목적지의 각 구간이 이어져있지 않으면 경로를 찾을 수 없다.")
    @Test
    void getPaths_notConnected() {

        //given
        long 부평역 = 지하철역_생성_요청_후_id_추출("부평역");
        long 구로역 = 지하철역_생성_요청_후_id_추출("구로역");
        long 신도림역 = 지하철역_생성_요청_후_id_추출("신도림역");
        long 영등포구청역 = 지하철역_생성_요청_후_id_추출("영등포구청역");

        지하철역_노선_등록_요청("일호선", "blue", 부평역, 구로역, 5);
        지하철역_노선_등록_요청("이호선", "red", 신도림역, 영등포구청역, 10);

        // 부평 - 5 - 구로 - 5 | 신도림 *** 10 *** 영등포구청

        //when
        ExtractableResponse<Response> 경로조회_결과 = 경로조회_요청(부평역, 영등포구청역);

        //then
        API_잘못된요청_응답코드_검사(경로조회_결과);
        출발지또는_목적지가_이어져있지않을때_에러메세지_검사(경로조회_결과);

    }

}
