package nextstep.subway.acceptance.line;

import io.restassured.path.json.JsonPath;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.controller.dto.LineCreateRequestBody;
import nextstep.subway.controller.dto.LineUpdateRequestBody;
import nextstep.subway.helper.api.LineApi;
import nextstep.subway.helper.api.StationApi;
import nextstep.subway.helper.fixture.LineFixture;
import nextstep.subway.helper.fixture.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static Long 강남역Id;
    private static Long 신논현역Id;
    private static Long 역삼역Id;

    @BeforeEach
    void createStationFixture() {
        강남역Id = StationApi.create(StationFixture.강남역).getLong("id");
        신논현역Id = StationApi.create(StationFixture.신논현역).getLong("id");
        역삼역Id = StationApi.create(StationFixture.역삼역).getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createSubwayLine() {
        // when
        LineCreateRequestBody 신분당선 = LineFixture.신분당선_생성_바디(강남역Id, 신논현역Id);
        LineApi.노선생성요청(신분당선);

        // then
        JsonPath getLinesResponseJson = LineApi.노선목록조회요청();

        assertThat(getLinesResponseJson.getList("color", String.class))
                .containsExactly(신분당선.getColor());

        assertThat(getLinesResponseJson.getList("name", String.class))
                .containsExactly(신분당선.getName());

        assertThat(getLinesResponseJson.getList("stations[0].id", Long.class))
                .containsExactly(신분당선.getUpStationId(), 신분당선.getDownStationId());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getSubwayLineList() {
        // given
        JsonPath 신분당선 = LineApi.노선생성요청(LineFixture.신분당선_생성_바디(강남역Id, 신논현역Id));
        JsonPath 이호선 = LineApi.노선생성요청(LineFixture.이호선_생성_바디(강남역Id, 역삼역Id));

        // when
        JsonPath response = LineApi.노선목록조회요청();

        // then
        assertThat(response.getList("name", String.class))
                .containsExactly(신분당선.getString("name"), 이호선.getString("name"));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * XXX: EntityNotFound 상황에 대한 테스트 추가
     */
    @DisplayName("지하철 노선 상세 정보를 조회한다.")
    @Test
    void getSubwayLine() {
        JsonPath 신분당선 = LineApi.노선생성요청(LineFixture.신분당선_생성_바디(강남역Id, 신논현역Id));

        // when
        JsonPath response = LineApi.노선조회요청(신분당선.getLong("id"));

        // then
        assertThat(response.getString("name"))
                .isEqualTo(신분당선.getString("name"));

        assertThat(response.getString("color"))
                .isEqualTo(신분당선.getString("color"));

        assertThat(response.getList("stations.id", Long.class))
                .isEqualTo(신분당선.getList("stations.id", Long.class)
        );
        assertThat(response.getString("distance")).isNullOrEmpty();
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateSubwayLine() {
        // given
        JsonPath 신분당선 = LineApi.노선생성요청(LineFixture.신분당선_생성_바디(강남역Id, 신논현역Id));

        // when
        LineUpdateRequestBody requestBody = new LineUpdateRequestBody("구분당선", "bg-blue-600");
        LineApi.노선수정요청(신분당선.getLong("id"), requestBody);

        // then
        JsonPath getLineJsonPath = LineApi.노선조회요청(신분당선.getLong("id"));

        assertThat(getLineJsonPath.getString("name"))
                .isEqualTo(requestBody.getName());

        assertThat(getLineJsonPath.getString("color"))
                .isEqualTo(requestBody.getColor());

        assertThat(getLineJsonPath.getList("stations.id", Long.class))
                .isEqualTo(신분당선.getList("stations.id", Long.class));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteSubwayLine() {
        // given
        JsonPath 신분당선 = LineApi.노선생성요청(LineFixture.신분당선_생성_바디(강남역Id, 신논현역Id));

        // when
        LineApi.노선삭제요청(신분당선.getLong("id"));

        // then
        assertThat(LineApi.노선목록조회요청()
                .getList("name", String.class))
                .doesNotContain(신분당선.getString("name"));
    }
}
