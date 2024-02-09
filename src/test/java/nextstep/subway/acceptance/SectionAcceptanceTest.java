package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.acceptance.LineSteps.노선을_조회한다;
import static nextstep.subway.acceptance.SectionSteps.구간을_등록한다;
import static nextstep.subway.acceptance.SectionSteps.구간을_제거한다;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {
    public static final String 강남역 = "강남역";
    public static final String 역삼역 = "역삼역";
    public static final String 선릉역 = "선릉역";
    public static final String 삼성역 = "삼성역";
    private Long 강남역Id;
    private Long 역삼역Id;
    private Long 선릉역Id;
    private Long 삼성역Id;

    @BeforeEach
    void init() {
        강남역Id = 지하철역_생성_요청(강남역).jsonPath().getLong("id");
        역삼역Id = 지하철역_생성_요청(역삼역).jsonPath().getLong("id");
        선릉역Id = 지하철역_생성_요청(선릉역).jsonPath().getLong("id");
        삼성역Id = 지하철역_생성_요청(삼성역).jsonPath().getLong("id");
    }

    /**
     * When 노선이 생성되어 있다.
     * And 노선에 이미 등록되어 있는 역을 등록한다.
     * Then 실패 코드가 리턴된다.
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
     * And 노선의 끝에 구간을 등록한다.
     * Then 정상 응답 처리된다.
     * And 노선을 조회하면 강남역 - 역삼역 - 선릉역이 조회된다.
     */
    @DisplayName("노선의 끝에 구간을 등록한다.")
    @Test
    public void 노선의_끝_구간등록_정상처리() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        구간이_정상_등록된다(response, HttpStatus.CREATED);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(강남역, 역삼역, 선릉역), 20);
    }

    /**
     * Given 노선이 생성되어 있다.
     * When 노선의 앞에 구간을 등록한다.
     * Then 정상 응답 처리 된다.
     * And 노선을 조회하면 강남역 - 역삼역 - 선릉역이 조회된다.
     */
    @DisplayName("노선의 맨앞에 구간을 등록한다.")
    @Test
    public void 노선의_맨_앞_구간등록_정상처리() {
        final Long lineId = 노선이_생성되어_있다("이호선", "bg-red-600", 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 강남역Id, 역삼역Id, 5);

        구간이_정상_등록된다(response, HttpStatus.CREATED);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(강남역, 역삼역, 선릉역), 15);
    }

    /**
     * Given 노선이 생성되어 있다.
     * When 노선의 중간에 구간을 등록한다.
     * Then 정상 응답 처리 된다.
     * And 노선을 조회하면 강남역 - 선릉역 - 삼성역이 조회된다.
     */
    @DisplayName("노선의 중간에 구간을 등록한다.")
    @Test
    public void 노선의_중간_구간등록_정상처리() {
        final Long lineId = 노선이_생성되어_있다("이호선", "bg-red-600", 강남역Id, 삼성역Id, 10);

        final ExtractableResponse<Response> response = 구간을_등록한다(lineId, 강남역Id, 선릉역Id, 5);

        구간이_정상_등록된다(response, HttpStatus.CREATED);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(강남역, 선릉역, 삼성역), 10);
    }

    /**
     * Given 노선이 생성되어 있다.
     * When 구간이 하나인 노선의 구간을 제거한다.
     * Then 실패코드가 리턴된다.
     */
    @DisplayName("노선의 구간을 제거할 때 구간이 1개인 경우 오류가 발생한다.")
    @Test
    public void 구간제거_구간이_하나일때() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 역삼역Id);

        예외가_발생한다(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선이 생성되어 있다.
     * And 구간이 등록되어 있다.
     * When 노선의 끝에 구간을 제거한다.
     * Then 정상 응답 처리된다.
     * And 노선을 조회하면 강남역 - 선릉역이 조회된다.
     */
    @DisplayName("노선의 끝의 구간을 제거한다.")
    @Test
    public void 노선의끝_구간제거_정상() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 선릉역Id);

        구간이_정상_제거된다(response, HttpStatus.NO_CONTENT);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(강남역, 역삼역), 10);
    }

    /**
     * Given 노선이 생성되어 있다.
     * And 구간이 등록되어 있다.
     * When 노선의 맨 앞의 구간을 제거한다.
     * Then 정상 응답 처리된다.
     * And 노선을 조회하면 역삼역 - 선릉역이 조회된다.
     */
    @DisplayName("노선의 끝의 구간을 제거한다.")
    @Test
    public void 노선의_맨_앞_구간제거_정상() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 강남역Id);

        구간이_정상_제거된다(response, HttpStatus.NO_CONTENT);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(역삼역, 선릉역), 10);
    }

    /**
     * Given 노선이 생성되어 있다.
     * And 구간이 등록되어 있다.
     * When 노선의 중간 구간을 제거한다.
     * Then 정상 응답 처리된다.
     * And 노선을 조회하면 역삼역 - 선릉역이 조회된다.
     */
    @DisplayName("노선의 중간 구간을 제거한다.")
    @Test
    public void 노선의_중간_구간제거_정상() {
        final Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);

        구간을_등록한다(lineId, 역삼역Id, 선릉역Id, 10);

        final ExtractableResponse<Response> response = 구간을_제거한다(lineId, 역삼역Id);

        구간이_정상_제거된다(response, HttpStatus.NO_CONTENT);

        노선을_조회하여_지하철역과_길이를_확인한다(lineId, Arrays.asList(강남역, 선릉역), 20);
    }

    public Long 노선이_생성되어_있다(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        return LineSteps.노선이_생성되어_있다(name, color, upStationId, downStationId, distance).as(LineResponse.class).getId();
    }

    private void 구간이_정상_제거된다(final ExtractableResponse<Response> response, final HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 구간이_정상_등록된다(final ExtractableResponse<Response> response, final HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 예외가_발생한다(final ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 노선을_조회하여_지하철역과_길이를_확인한다(Long lineId, List<String> stations, int totalDistance) {
        final ExtractableResponse<Response> response = 노선을_조회한다(lineId);
        final int distance = response.as(LineResponse.class).getDistance();
        final List<String> stationNames = response.jsonPath().getList("stations.name");

        assertThat(stationNames).containsExactlyElementsOf(stations);
        assertThat(distance).isEqualTo(totalDistance);
    }
}
