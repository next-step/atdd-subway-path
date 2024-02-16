package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.steps.StationSteps;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.steps.LineSteps.createLine;
import static nextstep.subway.steps.SectionSteps.createSection;
import static nextstep.subway.steps.SectionSteps.deleteSection;
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
     * then : 추가하는 구간이 노선의 마지막 역이 아니면 실패한다.
     */
    @Test
    void 추가하는_구간이_노선의_마지막역이_아니면_실패한다() {
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

        // when
        ExtractableResponse<Response> response = createSection(이호선_id, 선릉역_id, 강남역_id, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("구간의 상행역과 노선의 하행역이 일치하지 않습니다.");
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
     * given : 구간을 생성한다.
     * when : 생성한 구간을 제거한다.
     * then : 노선에서 구간이 제거된다.
     */
    @Test
    void 구간을_생성하고_구간을_제거한다() {
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
        ExtractableResponse<Response> response = deleteSection(이호선_id, 강남역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(204);
    }

    /**
     * given : 구간을 생성한다.
     * when : 생성한 구간을 제거한다.
     * then : 제거할 구간의 역이 하행종점역이 아니면 실패한다.
     */
    @Test
    void 마지막역이_아니면_구간을_제거가_실패한다() {
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
        ExtractableResponse<Response> response = deleteSection(이호선_id, 역삼역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("지하철 역이 일치하지 않습니다.");
    }

    /**
     * given : 구간을 생성한다.
     * when : 생성한 구간을 제거한다.
     * then : 제거할 구간의 역이 노선의 마지막 역이면 실패한다.
     */
    @Test
    void 노선에_1개의_역만_남으면_구간제거가_실패한다() {
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
        deleteSection(이호선_id, 역삼역_id);

        // when
        ExtractableResponse<Response> response = deleteSection(이호선_id, 선릉역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("지하철역이 충분하지 않습니다.");
    }
}