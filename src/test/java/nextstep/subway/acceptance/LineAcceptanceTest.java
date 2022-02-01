package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.exception.DuplicationException;
import nextstep.subway.applicaion.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.stream.Stream;

import static nextstep.subway.utils.LineStepUtil.기본주소;
import static nextstep.subway.utils.LineStepUtil.*;
import static nextstep.subway.utils.StationStepUtil.*;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {


    static Stream<Arguments> 노선파라미터_제공() {
        final int 종점간거리 = 2;
        final Long 상행종점 = 1L;
        final Long 하행종점 = 2L;

        return Stream.of(
                Arguments.of(노선파라미터생성(기존노선, 기존색상, 상행종점, 하행종점, 종점간거리),
                        노선파라미터생성(새로운노선, 새로운색상, 상행종점, 하행종점, 종점간거리))
        );
    }

    /**
     * Given 지하철 역 (상행, 하행)생성을 요청한다.
     */
    @BeforeEach
    void 사용될_지하철역들_생성() {
        지하철역생성(기존지하철);
        지하철역생성(새로운지하철);
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void 노선생성_테스트(Map<String, Object> 노선_파라미터) {
        //when
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선_파라미터);

        //then
        상태_값_검사(노선_생성_응답, HttpStatus.CREATED);
        ExtractableResponse<Response> 노선조회 = 노선조회(노선_생성_응답.header(HttpHeaders.LOCATION));
        단일_값_검사(노선조회, 노선_이름_키, 기존노선);
    }

    /**
     * When 없는 역을 종점으로 선언하고 노선 생성 요청을 한다
     * Then 지하철 노선 생성이 실패한다.
     */
    @DisplayName("없는 역을 노선에 등록한다")
    @Test
    void notFoundSection() {
        //when
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선파라미터생성(새로운노선, 새로운색상, Long.MAX_VALUE, Long.MIN_VALUE, Integer.MAX_VALUE));

        //then
        예외_검사(노선_생성_응답, NotFoundException.MESSAGE);
    }


    /**
     * Given 지하철 역 (상행, 하행)생성을 요청한다.
     * Given 지하철 노선을 생성한다.
     * Given 새로운 지하철 노선을 생성한다.
     * When 지하철 노선 조회를 요청한다.
     * Then 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void 노선목록조회_테스트(Map<String, Object> 첫_노선_파라미터, Map<String, Object> 두번째_노선_파라미터) {
        //given
        노선생성(첫_노선_파라미터);
        노선생성(두번째_노선_파라미터);

        //when
        ExtractableResponse<Response> 노선_조회_응답 = 노선조회(기본주소);

        //then
        리스트_값_검사(노선_조회_응답, 노선_이름_키, String.valueOf(첫_노선_파라미터.get(노선_이름_키)), String.valueOf(두번째_노선_파라미터.get(노선_이름_키)));
        리스트_값_검사(노선_조회_응답, 노선_색상_키, String.valueOf(첫_노선_파라미터.get(노선_색상_키)), String.valueOf(두번째_노선_파라미터.get(노선_색상_키)));
    }

    /**
     * Scenario 새로운 노선을 등록한다.
     * Given 지하철 노선을 생성하고
     * When 생성된 지하철 노선을 요청한다.
     * Then 생성된 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 조회")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void 노선조회_테스트(Map<String, Object> 노선_파라미터) {
        //given
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선_파라미터);

        //when
        ExtractableResponse<Response> 노선_조회_응답 = 노선조회(노선_생성_응답.header(HttpHeaders.LOCATION));

        //then
        상태_값_검사(노선_조회_응답, HttpStatus.OK);
        단일_값_검사(노선_조회_응답, 노선_이름_키, String.valueOf(노선_파라미터.get(노선_이름_키)));
        리스트_값_검사(노선_조회_응답, "stations." + 노선_이름_키, 기존지하철, 새로운지하철);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 수정을 요청한다,
     * Then 지하철 노선이 수정이 완료된다.
     */
    @DisplayName("지하철 노선 수정")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void 노선업데이트_테스트(Map<String, Object> 노선_파라미터) {
        //given
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선_파라미터);

        //when
        ExtractableResponse<Response> 노선_수정_응답 = 노선수정(노선_생성_응답);

        //then
        ExtractableResponse<Response> response = 노선조회(노선_생성_응답.header(HttpHeaders.LOCATION));

        상태_값_검사(노선_수정_응답, HttpStatus.OK);
        상태_값_검사(response, HttpStatus.OK);
        단일_값_검사(response, 노선_이름_키, 수정노선);
    }


    /**
     * Given 지하철 노선을 생성을 요청한다.
     * When 생성된 지하철 노선을 삭제를 요청한다.
     * Then 지하철 노선이 삭제된다.
     */
    @DisplayName("지하철 노선 삭제")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void 노선삭제_테스트(Map<String, Object> 노선_파라미터) {
        //given
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선_파라미터);

        //when
        ExtractableResponse<Response> 노선_삭제_응답 = 노선삭제(노선_생성_응답.header(HttpHeaders.LOCATION));

        //then
        상태_값_검사(노선_삭제_응답, HttpStatus.NO_CONTENT);
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 중복된 이름의 지하철 노선 생성을 요청한다.
     * Then 지하철 노선 생성이 실패한다.
     */

    @DisplayName("중복된 노선 생성은 실패한다")
    @MethodSource("노선파라미터_제공")
    @ParameterizedTest
    void duplicationLine(Map<String, Object> 노선_파라미터) {
        //given
        노선생성(노선_파라미터);

        //when
        ExtractableResponse<Response> 노선_생성_응답 = 노선생성(노선_파라미터);

        //then
        상태_값_검사(노선_생성_응답, HttpStatus.CONFLICT);
        예외_검사(노선_생성_응답, DuplicationException.MESSAGE);
    }

}
