package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.CommonAcceptanceTest;
import nextstep.subway.common.StationRestAssuredCRUD;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends CommonAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {

        // when
        String 강남역 = "강남역";
        ExtractableResponse<Response> response = StationRestAssuredCRUD.createStation(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = StationRestAssuredCRUD.showStations()
                .jsonPath().getList("name", String.class);

        assertThat(stationNames).containsAnyOf(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 생성하고 생성한 지하철역 목록을 조회한다.")
    @Test
    void findStationsTest() {

        //given
        String 강남역 = "강남역";
        String 선릉역 = "선릉역";
        StationRestAssuredCRUD.createStation(강남역);
        StationRestAssuredCRUD.createStation(선릉역);

        //when
        List<String> stationList = StationRestAssuredCRUD.showStations()
                .jsonPath().getList("name", String.class);

        //then
        assertThat(stationList).containsAll(List.of(강남역, 선릉역));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStationTest() {

        //given
        String 강남역 = "강남역";
        ExtractableResponse<Response> createResponse = StationRestAssuredCRUD.createStation(강남역);
        Long deleteId = createResponse.body().jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> deleteResponse = StationRestAssuredCRUD.deleteStation(deleteId);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<String> stationList = StationRestAssuredCRUD.showStations()
                .jsonPath().getList("name", String.class);

        assertThat(stationList).doesNotContain(강남역);
    }

}