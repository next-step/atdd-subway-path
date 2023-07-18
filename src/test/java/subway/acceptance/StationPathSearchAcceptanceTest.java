package subway.acceptance;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import utils.AcceptanceUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static utils.AcceptanceUtils.*;

@DisplayName("지하철 경로 조회 기능")
@Sql(scripts = "classpath:reset.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationPathSearchAcceptanceTest {

    /**
     * Given 1호선 (종로3가 -3KM- 종로5가 -5KM- 동대문 -5KM- 동묘앞)로 이뤄진 노선을 생성한다
     * Given 4호선 (혜화 -1KM- 동대문 -10KM- 동대문역사문화공원)로 이뤄진 노선을 생성한다
     * Given 부산2호선 (양산 -10KM- 남양산)로 이뤄진 노선을 생성한다
     */
    Map<String, Long> stationIdByName;

    @BeforeEach
    void setUp() {
        //given
        stationIdByName = createStationsAndGetStationMap(List.of("혜화", "동대문", "동대문역사문화공원", "종로3가", "종로5가", "동묘앞", "양산", "남양산"));

        final Long line1 = createStationLine("1호선", "blue", stationIdByName.get("종로3가"), stationIdByName.get("종로5가"), BigDecimal.valueOf(3L));
        createStationLineSection(line1, stationIdByName.get("종로5가"), stationIdByName.get("동대문"), BigDecimal.valueOf(5L));
        createStationLineSection(line1, stationIdByName.get("동대문"), stationIdByName.get("동묘앞"), BigDecimal.valueOf(5L));

        final Long line2 = createStationLine("4호선", "mint", stationIdByName.get("혜화"), stationIdByName.get("동대문"), BigDecimal.ONE);
        createStationLineSection(line2, stationIdByName.get("동대문"), stationIdByName.get("동대문역사문화공원"), BigDecimal.TEN);

        createStationLine("부산2호선", "red", stationIdByName.get("양산"), stationIdByName.get("남양산"), BigDecimal.TEN);
    }

    /**
     * When 종로3가에서 동대문역사문화공원으로 경로 조회를 요청한다
     * Then 종로3가에서 동대문역사문화공원으로 경로 역의 목록으로 (종로3가, 종로5가, 동대문, 동대문역사문화공원)를 응답한다
     * Then 전체 경로로 18을 응답한다
     */
    @DisplayName("정상적인 지하철 경로 조회")
    @Test
    void searchStationPath() {
        //when
        final JsonPath response = AcceptanceUtils.searchStationPath("종로3가", "동대문역사문화공원", HttpStatus.OK);
        final BigDecimal distance = response.getObject("distance", BigDecimal.class);
        final List<String> pathStationNames = response.getList("stations.name", String.class);

        //then
        final BigDecimal expectedDistance = BigDecimal.valueOf(18);
        Assertions.assertEquals(0, expectedDistance.compareTo(distance));
        Assertions.assertArrayEquals(List.of("종로3가", "종로5가", "동대문", "동대문역사문화공원").toArray(), pathStationNames.toArray());
    }

    /**
     * When 종로3가에서 종로3가 경로 조회를 요청한다
     * Then 에러 발생
     */
    @DisplayName("출발역과 도착역이 같은 경우의 지하철 경로 조회 시 에러")
    @Test
    void searchStationPath_Same_SourceStation_And_TargetStation() {
        //when & then
        AcceptanceUtils.searchStationPath("종로3가", "종로3가", HttpStatus.BAD_REQUEST);
    }

    /**
     * When 종로3가에서 양산역으로 경로 조회를 요청한다
     * Then 에러 발생
     */
    @DisplayName("출발역과 도착역이 노선상 연결되지 않은 경우 에러")
    @Test
    void searchStationPath_Not_Linked_SourceStation_And_TargetStation() {
        //when & then
        AcceptanceUtils.searchStationPath("종로3가", "양산", HttpStatus.BAD_REQUEST);
    }
}
