package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.ApiTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationSteps.지하철목록조회요청;
import static nextstep.subway.station.StationSteps.지하철삭제요청;
import static nextstep.subway.station.StationSteps.지하철생성요청;
import static nextstep.subway.station.StationSteps.지하철생성요청_다중생성;
import static nextstep.subway.station.StationSteps.지하철생성요청_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends ApiTest {

    @Autowired
    private StationRepository stationRepository;

    @AfterEach
    void tearDown() {
        stationRepository.deleteAllInBatch();
    }

    @DisplayName("지하철역을 생성하면 지하철역이 생성된다. 지하철역 목록 조회 시 생성한 역을 찾을 수 있다.")
    @Test
    void createStation() {
        // given
        StationRequest request = 지하철생성요청_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철생성요청(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철목록조회요청().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    @DisplayName("지하철역을 2개 생성하면 지하철역이 2개 생성된다. 지하철역 목록 조회 시 생성한 역을 찾을 수 있다.")
    @Test
    void showStations() {
        // given
        지하철생성요청_다중생성(List.of("강남역", "역삼역")).stream()
                .map(StationSteps::지하철생성요청)
                .forEach(x -> assertThat(x.statusCode()).isEqualTo(HttpStatus.CREATED.value()));

        // when
        ExtractableResponse<Response> response = 지하철목록조회요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).hasSize(2).containsExactly("강남역", "역삼역");
    }

    @DisplayName("지하철역을 생성하고 그 지하철역을 삭제하면 그 지하철 목록 조회시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteStation() {
        // given
        StationRequest request = 지하철생성요청_생성("강남역");
        ExtractableResponse<Response> createStation = 지하철생성요청(request);

        // when
        ExtractableResponse<Response> deleteStation = 지하철삭제요청(createStation.jsonPath().getLong("id"));

        // then
        assertThat(deleteStation.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> showStations = 지하철목록조회요청();
        assertThat(showStations.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(showStations.jsonPath().getList("name")).isEmpty();
    }
}
