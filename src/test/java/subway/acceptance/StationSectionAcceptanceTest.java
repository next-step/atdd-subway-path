package subway.acceptance;

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

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DisplayName("지하철 구간 관리 기능")
@Sql(scripts = "classpath:reset.sql", executionPhase = BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationSectionAcceptanceTest {

    private Long aStationId;
    private Long bStationId;
    private Long dStationId;
    private Long cStationId;

    @BeforeEach
    public void createStation() {
        final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역", "C역", "D역"));

        aStationId = stationIds.get(0);
        bStationId = stationIds.get(1);
        cStationId = stationIds.get(2);
        dStationId = stationIds.get(3);
    }

    /**
     * Given (A,B)역으로 거리가 8m인 1호선 노선을 생성한다
     * Given 1호선에 (B,C) 구간을 추가한다
     * When 1호선에 (A,D) 3m인 구간을 추가한다
     * Then 지하철 노선 조회 시 노선의 역 목록이 A,D,B,C순으로 조회된다
     */
    @DisplayName("정상적인 기존 노선의 역 구간 사이에 새로운 구간 추가")
    @Test
    void createStationLineSection_To_Between_Station() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.valueOf(8));
        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.valueOf(5));

        //when
        AcceptanceUtils.createStationLineSection(lineId, aStationId, dStationId, BigDecimal.valueOf(3), HttpStatus.OK);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("A역", "D역", "B역", "C역").toArray(), stationNames.toArray());
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한다
     * Given 1호선에 (B,C)로 구간을 추가한다
     * When 1호선에 (D,A) 구간을 추가한다
     * Then 지하철 노선 조회 시 노선의 역 목록이 D,A,B,C순으로 조회된다
     */
    @DisplayName("정상적으로 새로운 역을 기존 노선의 구간의 상행 종점 역으로 구간 추가")
    @Test
    void createStationLineSection_To_FirstUpStation() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);
        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.TEN);

        //when
        AcceptanceUtils.createStationLineSection(lineId, dStationId, aStationId, BigDecimal.ONE);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("D역", "A역", "B역", "C역").toArray(), stationNames.toArray());
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한다
     * Given 1호선 노선에 (B,C) 구간을 추가한다
     * When 1호선 노선에 (C,D) 구간을 추가한다
     * Then 지하철 노선 조회 시 노선의 역 목록이 A,B,C,D순으로 조회된다
     */
    @DisplayName("정상적으로 새로운 역을 기존 노선의 구간의 하행 종점 역으로 구간 추가")
    @Test
    void createStationLineSection_To_LastDownStation() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);
        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.TEN);

        //when
        AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.ONE);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("A역", "B역", "C역", "D역").toArray(), stationNames.toArray());
    }

    /**
     * Given (A,B)로 구간의 길이가 10m인 1호선 노선을 생성한다
     * When 1호선에 A,C로 길이가 12m인 구간을 추가한다
     * Then 에러 발생
     */
    @DisplayName("거리가 10m인 기존 노선의 역 구간 사이에 12m로 새로운 구간 추가시 에러")
    @Test
    void create_12M_StationLineSection_To_Between_Station_Has_10M() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

        //when & then
        AcceptanceUtils.createStationLineSection(lineId, aStationId, cStationId, BigDecimal.valueOf(12), HttpStatus.BAD_REQUEST);
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한다
     * Given 1호선 노선에 (B,C) 구간을 추가한다
     * When 1호선 노선에 (A,C) 구간을 추가한다
     * Then 에러 발생
     */
    @DisplayName("구간의 상행역과 하행역이 모두 노선에 등록된 역일 경우 구간 추가 시 에러")
    @Test
    void createStationLineSection_Both_Station_Existing_To_StationLine() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);
        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.TEN);

        //when & then
        AcceptanceUtils.createStationLineSection(lineId, aStationId, cStationId, BigDecimal.TEN, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한다
     * When 1호선 노선에 (C,D)로 구간을 추가한다
     * Then 에러 발생
     */
    @DisplayName("구간의 상행역과 하행역이 모두 노선에 포함되지 않은 역일 경우 구간 추가시 에러")
    @Test
    void createStationLineSection_Both_Station_NotExisting_To_stationLine() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

        //when & then
        AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.TEN, HttpStatus.BAD_REQUEST);
    }


    /**
     * Given (A,B)로 1호선 노선을 생성한
     * Given (B,C)를 길이 2로, (C,D)를 길이 5로 차례대로 1호선에 생성
     * When 1호선에서 C역 삭제
     * THEN 노선 조회시 지하철역 순서는 (A,B,D)가 된다
     */
    @DisplayName("정상적인 노선의 중간역을 삭제")
    @Test
    void deleteStationLineSection_Middle_Station() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.valueOf(2));
        AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.valueOf(5));

        //when
        AcceptanceUtils.deleteStationLineSection(lineId, cStationId, HttpStatus.OK);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("A역", "B역", "D역").toArray(), stationNames.toArray());
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한
     * Given (B,C), (C,D)구간을 차례대로 1호선에 생성
     * When 1호선에서 D역 삭제
     * THEN 노선 조회시 지하철역 순서는 (A,B,C)가 된다
     */
    @DisplayName("정상적인 노선의 하행종점역을 삭제")
    @Test
    void deleteStationLineSection_Last_Station() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.valueOf(2));
        AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.valueOf(5));

        //when
        AcceptanceUtils.deleteStationLineSection(lineId, dStationId, HttpStatus.OK);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("A역", "B역", "C역").toArray(), stationNames.toArray());
    }

    /**
     * Given (A,B)로 1호선 노선을 생성한
     * Given (B,C), (C,D)구간을 차례대로 1호선에 생성
     * When 1호선에서 A역 삭제
     * THEN 노선 조회시 지하철역 순서는 (B,C,D)가 된다
     */
    @DisplayName("정상적인 노선의 상행종점역을 삭제")
    @Test
    void deleteStationLineSection_First_Station() {
        //given
        final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

        AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.valueOf(2));
        AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.valueOf(5));

        //when
        AcceptanceUtils.deleteStationLineSection(lineId, dStationId, HttpStatus.OK);

        //then
        final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);
        Assertions.assertArrayEquals(List.of("B역", "C역","D역").toArray(), stationNames.toArray());
    }
}
