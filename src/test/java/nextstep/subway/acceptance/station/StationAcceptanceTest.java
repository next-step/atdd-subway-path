package nextstep.subway.acceptance.station;

import static nextstep.subway.acceptance.common.SubwayUtils.responseToLocation;
import static nextstep.subway.acceptance.common.SubwayUtils.responseToNames;
import static nextstep.subway.acceptance.station.StationUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(강남역);
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> 지하철역_목록 = responseToNames(지하철역_목록조회());
        assertThat(지하철역_목록).containsAnyOf(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void showStations() {
        // given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성(강남역);
        assertThat(강남역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> 역삼역_생성_응답 = 지하철역_생성(역삼역);
        assertThat(역삼역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        List<String> 지하철역_목록 = responseToNames(지하철역_목록조회());

        // then
        assertThat(지하철역_목록).hasSize(2);
        assertThat(지하철역_목록).containsExactlyInAnyOrder(강남역, 역삼역);

    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(강남역);
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        지하철역_삭제(responseToLocation(지하철역_생성_응답));

        // then
        List<String> 지하철역_목록 = responseToNames(지하철역_목록조회());
        assertThat(지하철역_목록).hasSize(0);
        assertThat(지하철역_목록).doesNotContain(강남역);
    }
}