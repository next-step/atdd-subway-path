package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.utils.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 기능 인수 테스트")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    private static final String 상행종점역명 = "상행종점역";
    private static final String 하행종점역명 = "하행종점역";
    private static final String 새로운역명 = "새로운역";
    private static final int 상행종점역_하행종점역_거리 = 10;
    private Long 상행종점역_id;
    private Long 하행종점역_id;
    private Long 새로운역_id;
    private Long 노선_id;

    @BeforeEach
    public void setUpFixture() {
        상행종점역_id = StationSteps.지하철역_생성_요청(상행종점역명);
        하행종점역_id = StationSteps.지하철역_생성_요청(하행종점역명);
        새로운역_id = StationSteps.지하철역_생성_요청(새로운역명);
        노선_id = LineSteps.지하철_노선_생성_요청(
                "칠호선",
                상행종점역_id,
                하행종점역_id,
                상행종점역_하행종점역_거리
        );
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 상행 종점역을 상행으로 새로운 역을 하행으로 등록한다
     * Then: 노선 조회시 상행 종점역, 새로운 역, 하행 종점역 순으로 조회된다
     */
    @Test
    void 역_사이에_새로운_역을_등록하는_경우() {
        //when
        LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(상행종점역_id, 새로운역_id, 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청_역_이름_목록_반환(노선_id);
        assertThat(stationNames).containsExactly(상행종점역명, 새로운역명, 하행종점역명);
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 상행 종점역을 하행으로 새로운 역을 상행으로 등록한다
     * Then: 노선 조회시 새로운 역, 상행 종점역, 하행 종점역 순으로 조회된다
     */
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        //when
        LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(새로운역_id, 상행종점역_id, 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청_역_이름_목록_반환(노선_id);
        assertThat(stationNames).containsExactly(새로운역명, 상행종점역명, 하행종점역명);
    }


    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 하행 종점역을 상행으로 새로운 역을 하행으로 등록한다
     * Then: 노선 조회시 상행 종점역, 하행 종점역, 새로운 역 순으로 조회된다
     */
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        //when
        LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(하행종점역_id, 새로운역_id, 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청_역_이름_목록_반환(노선_id);
        assertThat(stationNames).containsExactly(상행종점역명, 하행종점역명, 새로운역명);
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 하행 종점역을 하행으로 새로운 역을 상행으로 하고 상행 종점역과 하행 종점역 사이와 거리가 같은 구간을 등록한다
     * Then: 예외를 발생한다.
     */
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        //when
        ExtractableResponse<Response> response =
                LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(상행종점역_id, 새로운역_id, 상행종점역_하행종점역_거리));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * Given: 상행 종점역을 상행으로 새로운 역을 하행으로 등록한다
     * When: 상행 종점역을 상행으로 하행 종점역을 하행으로 하는 구간을 등록한다
     * Then: 예외를 발생한다.
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        //given
        LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(상행종점역_id, 새로운역_id, 5));

        //when
        ExtractableResponse<Response> response =
                LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(상행종점역_id, 하행종점역_id, 10));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 상행 종점역과 중간역 그리고 하행 종점역을 갖는 노선을 생성한다
     * When: 새로운 역을 상행으로 또 새로운 역을 하행으로 하는 구간을 등록한다
     * Then: 예외를 발생한다.
     */
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        //when
        Long 다른새로운역_id = StationSteps.지하철역_생성_요청("다른새로운역");
        ExtractableResponse<Response> response =
                LineSteps.지하철_노선_구간_등록_요청(노선_id, new SectionRequest(다른새로운역_id, 새로운역_id, 10));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 상행 종점역과 중간역 그리고 하행 종점역을 갖는 노선을 생성한다
     * When: 중간역을 삭제하면
     * Then: 노선 조회시 중간역은 조회되지 않는다.
     * Then: 노선 조회시 노선의 총 길이는 상행 종점역과 중간역 사이의 거리와 중간역과 하행 종점역 사이의 거리를 합한 값이다.
     */
    @Test
    void 구간_삭제_기능() {
        //given

        //when

        //then
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 상행 종점역을 삭제하면
     * Then: 예외가 발생한다.
     */
    @Test
    void 구간이_하나인_노선에서_상행_종점역을_제거할_때() {
        //given

        //when

        //then
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 상행 종점역을 삭제하면
     * Then: 예외가 발생한다.
     */
    @Test
    void 구간이_하나인_노선에서_하행_종점역을_제거할_때() {
        //given

        //when

        //then
    }

    /**
     * Given: 상행 종점역과 중간역 그리고 하행 종점역을 갖는 노선을 생성한다
     * When: 새로운역을 삭제하면
     * Then: 예외가 발생한다
     */
    @Test
    void 노선에_등록되어있지_않은_역을_제거하려할_때() {
        //given

        //when

        //then
    }
}
