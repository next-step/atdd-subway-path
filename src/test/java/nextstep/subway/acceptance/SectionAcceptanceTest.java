package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.SectionSteps.구간을_등록한다;
import static nextstep.subway.acceptance.SectionSteps.구간을_제거한다;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {
    private Long 강남역Id;
    private Long 역삼역Id;
    private Long 선릉역Id;

    @BeforeEach
    void init() {
        강남역Id = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역Id = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        선릉역Id = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
    }

    /**
     * When 노선이 생성되어 있다.
     * 노선의 하행종점역이 아닌 상행역의 구간을 등록한다.
     * Then 예외가 발생한다.
     */
    @DisplayName("노선에 구간을 등록 할 때, 구간의 상행역이 노선의 하행좀정역이 아니면 오류가 발생한다.")
    @Test
    public void 새로운_구간의_상행역이_노선의_하행종점역이_아닐_떄() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 선릉역Id, 역삼역Id, 10);

        예외가_발생한다(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 노선이 생성되어 있다.
     * 노선에 이미 등록되어 있는 역을 등록한다.
     * Then 예외가 발생한다.
     */
    @DisplayName("노선에 구간을 등록 할 때, 노선에 이미 등록되어 있는 역을 등록하면 오류가 발생한다.")
    @Test
    public void 이미_등록되어_있는_지하철역_일_때() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 역삼역Id, 강남역Id, 10);

        예외가_발생한다(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 노선이 생성되어 있다.
     * 노선의 끝에 구간을 등록한다.
     * Then 정상 응답 처리된다.
     */
    @DisplayName("노선의 끝에 구간을 등록한다.")
    @Test
    public void 노선의_끝_구간등록_정상처리() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        구간이_정상_등록한다(response, HttpStatus.CREATED);
    }

    /**
     * Given 노선이 생성되어 있다.
     * When 노선의 중간에 구간을 등록한다.
     * Then 정상 응답 처리 된다.
     * And 중간에 구간을 추가했기 때문에 노선을 조회하면, 구간 추가 전과 노선의 길이가 동일하다.
     */
    @DisplayName("노선의 중간에 구간을 등록한다.")
    @Test
    public void 노선의_중간_구간등록_정상처리() {
        final Long lineId = 노선이_생성되어_있다("이호선", "bg-red-600", 강남역Id, 선릉역Id, 10);
        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 강남역Id, 역삼역Id, 5);
        구간이_정상_등록한다(response, HttpStatus.CREATED);
        노선의_길이가_동일하다(lineId, 10);
    }

    /**
     * When 노선이 생성되어 있다.
     * 구간이 등록되어 있다.
     * 마지막 구간이 아닌 구간을 제거한다.
     * Then 예외가 발생한다.
     */
    @DisplayName("노선의 구간을 제거할 때 마지막구간이 아니면 오류가 발생한다.")
    @Test
    public void 구간제거_마지막구간이_아닐때() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 1L);

        예외가_발생한다(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 노선이 생성되어 있다.
     * 구간이 하나인 노선의 구간을 제거한다.
     * Then 예외가 발생한다.
     */
    @DisplayName("노선의 구간을 제거할 때 구간이 1개인 경우 오류가 발생한다.")
    @Test
    public void 구간제거_구간이_하나일때() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 역삼역Id);

        예외가_발생한다(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 노선이 생성되어 있다.
     * 구간이 등록되어 있다.
     * 구간을 제거한다.
     * Then 정상 응답 처리된다.
     */
    @DisplayName("노선의 구간을 제거한다.")
    @Test
    public void 구간제거_정상() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 선릉역Id);

        구간이_정상_제거된다(response, HttpStatus.NO_CONTENT);
    }

    public Long 노선이_생성되어_있다(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        return LineSteps.노선이_생성되어_있다(name, color, upStationId, downStationId, distance).as(LineResponse.class).getId();
    }

    private void 구간이_정상_제거된다(final ExtractableResponse<Response> response, final HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 구간이_정상_등록한다(final ExtractableResponse<Response> response, final HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 예외가_발생한다(final ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private static void 노선의_길이가_동일하다(final Long lineId, final int distance) {
        final LineResponse lineResponse = LineSteps.노선을_조회한다(lineId).as(LineResponse.class);
        assertThat(lineResponse.getDistance()).isEqualTo(distance);
    }
}
