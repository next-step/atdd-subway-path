package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.step.StationStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        String stationName = "강남역";
        ExtractableResponse<Response> responseOfCreateStation = StationStep.지하철역을_생성한다(stationName);

        // then
        assertThat(responseOfCreateStation.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> responseOfShowStations = StationStep.지하철역_목록을_조회한다();
        List<String> stationNames = 지하철역_목록_이름을_추출한다(responseOfShowStations);

        assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void findAllStations() {
        //given
        String 가양역 = "가양역";
        StationStep.지하철역을_생성한다(가양역);

        String 여의도역 = "여의도역";
        StationStep.지하철역을_생성한다(여의도역);

        //when
        ExtractableResponse<Response> response = StationStep.지하철역_목록을_조회한다();

        //then
        List<String> stationNames = 지하철역_목록_이름을_추출한다(response);

        assertThat(stationNames).hasSize(2);
        assertThat(stationNames).contains(가양역, 여의도역);
    }

    private List<String> 지하철역_목록_이름을_추출한다(ExtractableResponse<Response> responseOfShowStations) {
        return responseOfShowStations.jsonPath().getList("name", String.class);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStationById() {
        //given
        String 가양역 = "가양역";
        ExtractableResponse<Response> responseOfCreateStation = StationStep.지하철역을_생성한다(가양역);
        long stationId = 지하철역_Id를_추출한다(responseOfCreateStation);

        //when
        ExtractableResponse<Response> responseOfDeleteStation = StationStep.지하철역을_삭제한다(stationId);

        //then
        assertThat(responseOfDeleteStation.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> responseOfShowStationsAfterDelete = StationStep.지하철역_목록을_조회한다();
        List<Long> stationIdsAfterDelete = 지하철역_목록_Id를_추출한다(responseOfShowStationsAfterDelete);

        assertThat(stationIdsAfterDelete).doesNotContain(stationId);
    }

    private long 지하철역_Id를_추출한다(ExtractableResponse<Response> responseOfCreateStation) {
        return responseOfCreateStation.jsonPath().getLong("id");
    }

    private List<Long> 지하철역_목록_Id를_추출한다(ExtractableResponse<Response> responseOfShowStations) {
        return responseOfShowStations.jsonPath().getList("id", Long.class);
    }
}