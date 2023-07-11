package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.utils.AbstractAcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 기능 인수 테스트")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    private static final String 상행종점역명 = "상행종점역";
    private static final String 하행종점역명 = "하행종점역";
    private static final String 새로운역명 = "새로운역";
    private static final int 상행종점역_하행종점역_거리 = 10;
    private StationResponse 상행종점역;
    private StationResponse 하행종점역;
    private StationResponse 새로운역;
    private LineResponse 노선;

    @BeforeEach
    public void setUpFixture() {
        상행종점역 = StationSteps.지하철역_생성_요청(상행종점역명);
        하행종점역 = StationSteps.지하철역_생성_요청(하행종점역명);
        새로운역 = StationSteps.지하철역_생성_요청(새로운역명);
        노선 = LineSteps.지하철_노선_생성_요청(
                "칠호선",
                상행종점역.getId(),
                하행종점역.getId(),
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
        LineSteps.지하철_노선_구간_등록_요청(노선.getId(), new SectionRequest(상행종점역.getId(), 새로운역.getId(), 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청(노선.getId()).getStations()
                .stream().map(StationResponse::getName).collect(Collectors.toList());
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
        LineSteps.지하철_노선_구간_등록_요청(노선.getId(), new SectionRequest(새로운역.getId(), 상행종점역.getId(), 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청(노선.getId()).getStations()
                .stream().map(StationResponse::getName).collect(Collectors.toList());
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
        LineSteps.지하철_노선_구간_등록_요청(노선.getId(), new SectionRequest(하행종점역.getId(), 새로운역.getId(), 5));

        //then
        List<String> stationNames = LineSteps.지하철_노선_조회_요청(노선.getId()).getStations()
                .stream().map(StationResponse::getName).collect(Collectors.toList());
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
                LineSteps.지하철_노선_구간_등록_요청(노선.getId(), new SectionRequest(상행종점역.getId(), 새로운역.getId(), 상행종점역_하행종점역_거리));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given: 상행 종점역과 하행 종점역을 갖는 노선을 생성한다
     * When: 상행 종점역을 상행으로 하행 종점역을 하행으로 하는 구간을 등록한다
     * Then: 예외를 발생한다.
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        //when
        ExtractableResponse<Response> response =
                LineSteps.지하철_노선_구간_등록_요청(노선.getId(), new SectionRequest(상행종점역.getId(), 하행종점역.getId(), 5));

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
        //given

        //when

        //then
    }
}
