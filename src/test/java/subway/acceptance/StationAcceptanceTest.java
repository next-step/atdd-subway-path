package subway.acceptance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import utils.AcceptanceUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DisplayName("지하철역 관련 기능")
@Sql(scripts = "classpath:reset.sql", executionPhase = BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        AcceptanceUtils.createStation("강남역");

        // then
        final List<String> stationNames = AcceptanceUtils.getStations().getList("name", String.class);

        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역의 목록 조회")
    @Test
    void getStations() {
        //given
        AcceptanceUtils.createStations(List.of("수유역", "강변역"));

        //when
        final List<String> resultStationNames = AcceptanceUtils.getStations().getList("name", String.class);

        //then
        Assertions.assertEquals(2, resultStationNames.size());
        Assertions.assertEquals("수유역", resultStationNames.get(0));
        Assertions.assertEquals("강변역", resultStationNames.get(1));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        //given
        final long stationId = AcceptanceUtils.createStation("홍대입구역");

        //when
        AcceptanceUtils.deleteStation(stationId);

        //then
        final List<String> getStationsResponse = AcceptanceUtils.getStations().getList("name", String.class);

        Assertions.assertEquals(0, getStationsResponse.size());
    }
}
