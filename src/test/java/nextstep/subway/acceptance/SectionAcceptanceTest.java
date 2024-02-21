package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.steps.LineSteps;
import nextstep.subway.steps.StationSteps;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.steps.LineSteps.createLine;
import static nextstep.subway.steps.LineSteps.getLine;
import static nextstep.subway.steps.SectionSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }

    /**
     * given : 역과 노선을 생성한다.
     * when : 새로운 역을 구간으로 등록한다.
     * then : 추가한 역이 마지막 역인지 확인한다.
     */
    @Test
    void 구간을_생성하고_노선에_추가한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);

        // when
        ExtractableResponse<Response> response = createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("downStationId")).isEqualTo(3);
        assertThat(response.jsonPath().getLong("upStationId")).isEqualTo(2);
    }


    /**
     * given : 역과 노선을 생성한다.
     * when : 새로운 역을 구간으로 등록한다.
     * then : 추가하는 구간이 이미 노선에 존재하는 역이면 실패한다.
     */
    @Test
    void 추가하는_구간이_이미_노선에_존재하는_역이면_실패한다() {
        // given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // when
        ExtractableResponse<Response> response = createSection(이호선_id, 강남역_id, 역삼역_id, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("이미 등록된 상태입니다.");
    }

    /**
     * given : 노선을 생성하고
     * when : 노선의 마지막 구간에 새로운 구간을 추가하면
     * then : 새로운 구간이 등록된다.
     */
    @Test
    void 노선의_마지막에_구간을_추가한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        // when
        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);

        ExtractableResponse<Response> response = createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("downStationId")).isEqualTo(3);
        assertThat(response.jsonPath().getLong("upStationId")).isEqualTo(2);
    }

    /**
     * given : 노선을 생성하고 노선의 중간 구간에 새로운 구간을 추가한다.
     * when : 이때 새로운 역은 파라미터의 downStationId에 둘 때
     * then : 새로운 구간이 등록된다.
     */
    @Test
    void 노선의_중간에_구간을_추가한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");
        Long 교대역_id = StationSteps.createStation("교대역");

        // when
        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 교대역_id, 30);

        ExtractableResponse<Response> response = createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("downStationId")).isEqualTo(4);
        assertThat(response.jsonPath().getLong("upStationId")).isEqualTo(3);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(10);
    }

    /**
     * given : 노선을 생성하고 노선의 중간 구간에 새로운 구간을 추가한다.
     * when : 이때 새로운 역은 파라미터의 upStationId에 둘 때
     * then : 새로운 구간이 등록된다.
     */
    @Test
    void 노선의_중간에_구간을_추가한다_2() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");
        Long 교대역_id = StationSteps.createStation("교대역");

        // when
        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 교대역_id, 30);

        ExtractableResponse<Response> response = createSection(이호선_id, 강남역_id, 교대역_id, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getLong("downStationId")).isEqualTo(4);
        assertThat(response.jsonPath().getLong("upStationId")).isEqualTo(3);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(10);
    }

    /**
     * given : 노선을 생성하고 구간을 추가한다
     * when : 등록된 구간으로 다시 구간을 추가할 때
     * then : 등록이 실패한다.
     */
    @Test
    void 이미_등록된_구간을_추가하면_실패한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");
        Long 교대역_id = StationSteps.createStation("교대역");

        // when
        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 교대역_id, 30);

        ExtractableResponse<Response> response = createSection(이호선_id, 역삼역_id, 교대역_id, 30);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("이미 등록된 상태입니다.");
    }

    @Test
    void 노선의_시작역을_제거한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // when
        deleteSection(이호선_id, 선릉역_id);

        // when
        ExtractableResponse<Response> response = getLine(이호선_id);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf(
                "역삼역", "강남역"
        );
    }

    @Test
    void 노선의_중간역을_제거한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // when
        deleteSection(이호선_id, 역삼역_id);

        // when
        ExtractableResponse<Response> response = getLine(이호선_id);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf(
                "선릉역", "강남역"
        );
    }

    /**
     * given : 구간을 생성한다.
     * when : 생성한 구간을 제거한다.
     * then : 노선에서 구간이 제거된다.
     */
    @Test
    void 노선의_마지막역을_제거한다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");
        Long 강남역_id = StationSteps.createStation("강남역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        createSection(이호선_id, 역삼역_id, 강남역_id, 20);

        // when
        deleteSection(이호선_id, 강남역_id);

        // when
        ExtractableResponse<Response> response = getLine(이호선_id);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf(
                "선릉역", "역삼역"
        );
    }

    @Test
    void 노선의_마지막_구간에_포함된_역을_제거할수있다() {
        //given
        Long 선릉역_id = StationSteps.createStation("선릉역");
        Long 역삼역_id = StationSteps.createStation("역삼역");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "2호선",
                "green",
                선릉역_id,
                역삼역_id,
                10
        );
        Long 이호선_id = createLine(lineCreateRequest);
        deleteSection(이호선_id, 역삼역_id);

        // when
        ExtractableResponse<Response> response = getLine(이호선_id);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo(null);
    }
}