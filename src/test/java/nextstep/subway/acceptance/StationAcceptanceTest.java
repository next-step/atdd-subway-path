package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록_Id_획득;
import static nextstep.subway.utils.StationTestRequests.지하철_역_리스트_반환;
import static nextstep.subway.utils.StationTestRequests.지하철_역_삭제;
import static nextstep.subway.utils.StationTestRequests.지하철_역_조회;
import static nextstep.subway.utils.StatusCodeAssertions.응답코드_검증;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import nextstep.subway.station.controller.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class StationAcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_등록("강남역");

        // then
        응답코드_검증(response, HttpStatus.CREATED);

        // then
        List<StationResponse> stations = 지하철_역_리스트_반환();
        assertThat(stations).hasSize(1);

        StationResponse 강남역 = stations.get(0);
        assertAll(
            () -> assertThat(강남역.getId()).isEqualTo(1L),
            () -> assertThat(강남역.getName()).isEqualTo("강남역")
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철 역을 조회한다.")
    @Test
    void showStations() {
        //given
        지하철_역_등록("판교역");
        지하철_역_등록("정자역");

        //when
        ExtractableResponse<Response> response = 지하철_역_조회();

        //then
        응답코드_검증(response, HttpStatus.OK);
        List<StationResponse> stations = response.jsonPath().getList("", StationResponse.class);
        assertThat(stations).hasSize(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철 역을 삭제한다")
    @Test
    void deleteStation() {
        //given
        Long savedId = 지하철_역_등록_Id_획득("판교역");

        //when
        ExtractableResponse<Response> response = 지하철_역_삭제(savedId);

        //then
        응답코드_검증(response, HttpStatus.NO_CONTENT);
        List<StationResponse> stations = 지하철_역_리스트_반환();
        assertThat(stations).isEmpty();
    }
}