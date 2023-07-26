package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.common.DependentTest;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 관련 기능")
@DependentTest
@Sql(scripts = {"/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SectionAcceptanceTest {

    private LineResponse 분당선;
    private StationResponse 야탑역;
    private StationResponse 이매역;

    private long DEFAULT_DISTANCE = 20L;

    @BeforeEach
    void init() {
        야탑역 = StationTestHelper.createStation("야탑역");
        이매역 = StationTestHelper.createStation("이매역");
        분당선 = LineTestHelper.createLine(new LineRequest("분당선", "bg-red-600", 야탑역.getId(), 이매역.getId(), DEFAULT_DISTANCE));
    }

    /**
     * GIVEN 지하철 노선이 등록되어 있고, 하나의 구간이 등록되어 있는 상태에서
     * WHEN 지하철 구간을 등록하면
     * THEN 지하철 구간이 등록된다.
     */
    @Test
    @DisplayName("지하철 구간을 등록한다.")
    void createSection() {
        // given
        StationResponse 서현역 = StationTestHelper.createStation("서현역");

        // when
        LineTestHelper.createSection(분당선.getId(), 이매역.getId(), 서현역.getId(), 10L);

        // then
        LineResponse lineResponse = LineTestHelper.selectLine(분당선.getId());
        assertThat(lineResponse.getStations())
                .hasSize(3)
                .extracting(StationResponse::getName)
                .containsExactly("야탑역", "이매역", "서현역");
    }


    /**
     * GIVEN 지하철 노선이 등록되어 있고, 하나의 구간이 등록되어 있는 상태에서
     * WHEN 중간에 구간을 추가하면
     * THEN 거리가 쪼개지고 중간구간으로 등록된다.
     */
    @Test
    @DisplayName("구간 중간에 구간을 추가하면 중간 구간이 생긴다.")
    void insertBetweenSection() {
        // given
        final StationResponse 중간역 = StationTestHelper.createStation("중간역");
        final long distance = 5L;

        // when
        LineTestHelper.createSection(분당선.getId(), 야탑역.getId(), 중간역.getId(), distance);
        final LineResponse lineResponse = LineTestHelper.selectLine(분당선.getId());

        // then
        assertThat(lineResponse.getStations())
            .hasSize(3)
            .extracting(StationResponse::getName)
            .containsExactly("야탑역", "중간역", "이매역");
    }

    /**
     * GIVEN 지하철 노선이 등록되어있고, 2개의 구간이 등록되어있는 상태에서
     * WHEN 하나의 역을 삭제하는 요청을 보내면
     * THEN 해당 구간이 제거되고 그 구간이 중간구간이라면 합쳐진다.
     */
    @Test
    @DisplayName("2개 이상 구간이 있을 때, 구간을 삭제할 수 있다")
    void deleteSection() {
        // given
        final long distance = 5L;
        final StationResponse 중간역 = StationTestHelper.createStation("중간역");
        LineTestHelper.createSection(분당선.getId(), 야탑역.getId(), 중간역.getId(), distance);
        final LineResponse lineResponse = LineTestHelper.selectLine(분당선.getId());

        // when
        LineTestHelper.deleteSection(분당선.getId(), 중간역.getId());

        // then
        final LineResponse afterResponse = LineTestHelper.selectLine(분당선.getId());

        assertThat(afterResponse.getStations())
            .extracting(StationResponse::getName)
            .containsExactly("야탑역", "이매역");
    }

    /**
     * GIVEN 지하철 노선이 등록되어있고, 1개의 구간이 등록되어있는 상태에서
     * WHEN 하나의 역을 삭제하는 요청을 보내면
     * THEN 에러가 발생한다
     */
    @Test
    @DisplayName("최소단위 구간만 있을 때, 구간을 삭제할 수 없다")
    void deleteSectionOnThrowError() {
        // when
        final var responseExtractableResponse = LineTestHelper.deleteSection(분당선.getId(), 야탑역.getId());

        // then
        assertThat(responseExtractableResponse)
            .extracting(ExtractableResponse::statusCode)
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
