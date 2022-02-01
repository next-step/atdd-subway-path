package nextstep.subway.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.exception.DuplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void 지하철역생성_테스트() {
        // when
        ExtractableResponse<Response> 지하철_생성_응답 = 지하철역생성(기존지하철);

        // then
        상태_값_검사(지하철_생성_응답, HttpStatus.CREATED);
        assertThat(지하철_생성_응답.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    /**
     * Given 지하철역 생성을 요청 한다
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void 지하철역목록조회_테스트() {
        /// given
        지하철역생성(기존지하철);
        지하철역생성(새로운지하철);

        // when
        ExtractableResponse<Response> 지하철_조회_응답 = 지하철역조회(기본주소);
        상태_값_검사(지하철_조회_응답, HttpStatus.OK);
        리스트_값_검사(지하철_조회_응답, 지하철_역_이름_키, 기존지하철, 새로운지하철);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void 지하철역삭제_테스트() {
        // given
        ExtractableResponse<Response> 지하철_생성_응답 = 지하철역생성(기존지하철);

        // when
        ExtractableResponse<Response> 지하철_삭제_응답 = 지하철역삭제(지하철_생성_응답.header(HttpHeaders.LOCATION));

        // then
        상태_값_검사(지하철_삭제_응답, HttpStatus.NO_CONTENT);
    }

    /**
     * Given 지하철 역을 생성한다.
     * When 동일한 이름의 지하철 역 생성을 요청한다.
     * Then 지하철 역 생성이 실패한다.
     */
    @DisplayName("중복된 지하철 역은 생성이 실패한다")
    @Test
    void 중복된지하철역생성_테스트() {
        // given
        지하철역생성(기존지하철);

        //when
        ExtractableResponse<Response> 지하철_생성_응답 = 지하철역생성(기존지하철);

        //then
        상태_값_검사(지하철_생성_응답, HttpStatus.CONFLICT);
        예외_검사(지하철_생성_응답, DuplicationException.MESSAGE);
    }
}
