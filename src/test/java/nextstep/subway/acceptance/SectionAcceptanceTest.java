package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.exception.DuplicationException;
import nextstep.subway.applicaion.exception.NotLastSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.LineStepUtil.*;
import static nextstep.subway.utils.SectionStepUtil.구간등록;
import static nextstep.subway.utils.SectionStepUtil.구간삭제요청;
import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    final int 종점간거리 = 7;
    ExtractableResponse<Response> 노선_생성_결과;
    private Long 상행종점;
    private Long 하행종점;
    private String 상행역_이름;
    private String 하행역_이름;

    /**
     * Given 노선 생성을 요청한다.
     */
    @BeforeEach
    void setup() {
        ExtractableResponse<Response> 지하철역생성1 = 지하철역생성(기존지하철);
        ExtractableResponse<Response> 지하철역생성2 = 지하철역생성(새로운지하철);

        상행종점 = 지하철역생성1.jsonPath().getLong(지하철_역_아이디_키);
        하행종점 = 지하철역생성2.jsonPath().getLong(지하철_역_아이디_키);
        상행역_이름 = 지하철역생성1.jsonPath().getString(지하철_역_이름_키);
        하행역_이름 = 지하철역생성2.jsonPath().getString(지하철_역_이름_키);
        노선_생성_결과 = 노선생성(노선파라미터생성(기존노선, 기존색상, 상행종점, 하행종점, 종점간거리));
    }

    /**
     * Given 상행이 될 지하철 역 생성
     * Given 노선 등록을 요청한다
     * When 새로운 구간 등록을 요청한다
     * Then  구간 등록이 완료된다.
     */
    @DisplayName("새로운 구간을 등록한다")
    @Test
    void 새로운_구간_등록_테스트() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);

        //when
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(하행종점, 지하철역_ID, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.CREATED);
        ExtractableResponse<Response> 노선_조회_결과 = 노선조회(노선_생성_결과.header(HttpHeaders.LOCATION));
        assertThat(노선_조회_결과.jsonPath().getList("stations." + 노선_이름_키).size()).isEqualTo(3);
    }

    /**
     * Given 상행과 하행 사이에 들어갈 역을 생성한다
     * When  노선에 중간 구간 생성을 요청한다.
     * Then  구간 생성이 성공한다.
     */
    @DisplayName("상행과 하행역 중간에 새로운 역을 등록할 수 있다.")
    @Test
    void 구간_사이에_구간을_등록할_수_있다() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);

        //when
        int 종점간거리 = 3;
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(상행종점, 지하철역_ID, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.CREATED);
        ExtractableResponse<Response> 노선_조회_결과 = 노선조회(노선_생성_결과.header(HttpHeaders.LOCATION));
        리스트_값_검사(노선_조회_결과, "stations." + 노선_이름_키, 상행역_이름, 하행역_이름, "아무개");
    }

    /**
     * When 신규 구간을 기존 구간 맨 앞에 추가한다
     * Then 신규 구간이 등록된다.
     */
    @DisplayName("신규 구간을 기존 구간 맨 앞에 추가할 수 있다")
    @Test
    void 구간_맨_앞에_구간을_등록할_수_있다() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);

        //when
        int 종점간거리 = 3;
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(지하철역_ID, 상행종점, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.CREATED);
        ExtractableResponse<Response> 노선_조회_결과 = 노선조회(노선_생성_결과.header(HttpHeaders.LOCATION));
        리스트_값_검사(노선_조회_결과, "stations." + 노선_이름_키, 상행역_이름, 하행역_이름, "아무개");
    }

    /**
     * When 신규 구간을 기존 구간 맨 뒤에 추가한다
     * Then 신규 구간이 등록된다.
     */
    @DisplayName("신규 구간을 기존 구간 맨 뒤에 추가할 수 있다")
    @Test
    void 구간_맨_뒤에_구간을_등록할_수_있다() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);

        //when
        int 종점간거리 = 3;
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(하행종점, 지하철역_ID, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.CREATED);
        ExtractableResponse<Response> 노선_조회_결과 = 노선조회(노선_생성_결과.header(HttpHeaders.LOCATION));
        리스트_값_검사(노선_조회_결과, "stations." + 노선_이름_키, 상행역_이름, 하행역_이름, "아무개");
    }

    /**
     * When 신규 구간을 기존 구간 맨 뒤에 추가한다
     * Then 신규 구간등록이 실패한다.
     */
    @DisplayName("구간을 중간에 추가 시 기존의 역간 거리를 넘을 수 없다")
    @Test
    void 구간을_중간에_추가_시_기존_길이를_넘을_수_없다() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);

        //when
        int 종점간거리 = Integer.MAX_VALUE;
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(상행종점, 지하철역_ID, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When  상행역,하행역이 이미 노선에 등록된 구간 등록을 요청을 한다.
     * Then  구간 등록이 실패한다.
     */
    @DisplayName("신규 구간 등록 시 이미 노선에 모두 등록된 역들이면 구간 등록이 실패한다")
    @Test
    void 모든_역이_이미_등록_되어있다면_실패한다() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);
        int 종점간거리 = this.종점간거리 - 1;
        구간등록(상행종점, 지하철역_ID, 종점간거리);

        //when
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(상행종점, 하행종점, 종점간거리);

        //then
        상태_값_검사(구간_등록_응답, HttpStatus.CONFLICT);
        예외_검사(구간_등록_응답, DuplicationException.MESSAGE);

    }


    /**
     * When  새로운 상행이 기존의 하행과 일치하지 않는 구간 등록을 요청한다
     * Then  구간 등록이 실패한다
     */
    @Disabled
    @DisplayName("새로운 구간의 상행은 기존 구간의 하행과 일치해야한다.")
    @Test
    void 잘못된_상행_하행_구간_등록_테스트() {
        //when
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(상행종점, 하행종점, 종점간거리);

        상태_값_검사(구간_등록_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When  등록되지않은 하행역 구간을 요청한다.
     * Then  구간 등록이 실패한다
     */
    @DisplayName("기존 구간에 없는 역을 새로운 구간의 하행역으로 생성은 불가하다.")
    @Test
    void 등록안된_하행구간_등록_테스트() {
        //when
        Long 없는지하철역 = Long.MAX_VALUE;
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(하행종점, 없는지하철역, 종점간거리);

        //ten
        상태_값_검사(구간_등록_응답, HttpStatus.NOT_FOUND);
    }

    /**
     * Given 구간 등록을 요청한다
     * When  하행 종점인 구간 삭제를 요청한다.
     * Then 삭제 된다.
     */
    @DisplayName("마지막 구간만 삭제가 가능하다.")
    @Test
    void 하행종점_구간_삭제() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);
        ExtractableResponse<Response> 구간_등록_응답 = 구간등록(하행종점, 지하철역_ID, 종점간거리);

        //when
        Long 하행_지하철역_ID = 구간_등록_응답.jsonPath().getLong("downStation." + 지하철_역_아이디_키);
        ExtractableResponse<Response> 구간_삭제_응답 = 구간삭제요청(하행_지하철역_ID);

        //then
        상태_값_검사(구간_삭제_응답, HttpStatus.NO_CONTENT);
    }


    /**
     * Given 구간 등록을 요청한다
     * When  하행 종점이 아닌 구간 삭제를 요청한다.
     * Then 삭제 되지 않는다.
     */
    @DisplayName("마지막 구간만 삭제가 가능하다.")
    @Test
    void 하행종점_구간만_삭제가능() {
        //given
        ExtractableResponse<Response> 아무개 = 지하철역생성("아무개");
        Long 지하철역_ID = 아무개.jsonPath().getLong(지하철_역_아이디_키);
        구간등록(하행종점, 지하철역_ID, 종점간거리);

        //when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간삭제요청(하행종점);

        //then
        상태_값_검사(구간_삭제_응답, HttpStatus.BAD_REQUEST);
        예외_검사(구간_삭제_응답, NotLastSectionException.MESSAGE);
    }

    /**
     * When  하나 남은 구간을 삭제 요청한다.
     * Then 삭제 요청이 실패한다.
     */
    @DisplayName("하나 남은 구간은 삭제가 불가능하다")
    @Test
    void 마지막_구간만_삭제가능() {
        //when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간삭제요청(하행종점);

        //then
        상태_값_검사(구간_삭제_응답, HttpStatus.BAD_REQUEST);
        예외_검사(구간_삭제_응답, "마지막 구간 삭제 불가");
    }

}
