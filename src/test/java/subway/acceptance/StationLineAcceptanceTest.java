package subway.acceptance;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import utils.AcceptanceUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DisplayName("지하철 노선 관련 기능")
@Sql(scripts = "classpath:reset.sql", executionPhase = BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationLineAcceptanceTest {

	/**
	 * Given 수유역,강변역을 생성한다
	 * When 수유역,강변역으로 4번 노선을 생성한다
	 * Then 4번노선 조회 시 생성한 노선을 조회 할 수 있다
	 */
	@DisplayName("지하철 노선을 생성한다")
	@Test
	void createStationLine() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("수유역", "강변역"));

		//when
		final Long lineId = AcceptanceUtils.createStationLine("4호선", "blue", stationIds.get(0), stationIds.get(1), BigDecimal.TEN);

		//then
		final List<Long> lineIds = AcceptanceUtils.getStationLines().getList("id", Long.class);

		Assertions.assertTrue(lineIds.contains(lineId));
	}

	/**
	 * Given 시청역,서울역 생성한다
	 * Given 신촌역,홍대입구역 생성한다
	 * Given 시청역,서울역으로 1호선 노선을 생성한다
	 * Given 신촌역,홍대입구역으로 2호선 노선을 생성한다
	 * WHEN 지하철 노선 목록을 조회한다
	 * Then 생성했던 노선의 개수만큼 노선의 목록이 조회된다
	 */
	@DisplayName("지하철 노선 목록 조회한다")
	@Test
	void getStationLines() {
		//given
		final List<Long> line1_stationIds = AcceptanceUtils.createStations(List.of("시청역", "서울역"));
		final List<Long> line2_stationIds = AcceptanceUtils.createStations(List.of("신촌역", "홍대입구역"));

		AcceptanceUtils.createStationLine("1호선", "blue", line1_stationIds.get(0), line1_stationIds.get(1), BigDecimal.ONE);
		AcceptanceUtils.createStationLine("2호선", "green", line2_stationIds.get(0), line2_stationIds.get(1), BigDecimal.TEN);

		//when
		final JsonPath jsonPath = AcceptanceUtils.getStationLines();

		//then
		final List<Long> lineIds = jsonPath.getList("id", Long.class);

		Assertions.assertEquals(2, lineIds.size());
	}

	/**
	 * Given 수유역,강변역을 생성한다
	 * Given 수유역,강변역으로 4번 노선을 생성한다
	 * WHEN 4번노선을 조회한다
	 * Then 4번노선의 정보와 역의 목록이 조회된다
	 */
	@DisplayName("지하철 노선 조회한다")
	@Test
	void getStationLine() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("수유역", "강변역"));
		final Long lineId = AcceptanceUtils.createStationLine("4호선", "blue", stationIds.get(0), stationIds.get(1), BigDecimal.TEN);

		//when
		final JsonPath jsonPath = AcceptanceUtils.getStationLine(lineId);

		//then
		Assertions.assertEquals("4호선", jsonPath.getString("name"));
		Assertions.assertEquals("blue", jsonPath.getString("color"));
		Assertions.assertEquals("수유역", jsonPath.getString("stations[0].name"));
		Assertions.assertEquals("강변역", jsonPath.getString("stations[1].name"));
	}

	/**
	 * Given 수유역,강변역을 생성한다
	 * Given 수유역,강변역으로 4번 노선을 생성한다
	 * WHEN 4번 노선을 이름, 색깔 정보를 수정한다
	 * Then 지하철 노선 조회 시 변경된 이름, 색깔 정보로 조회된다
	 */
	@DisplayName("지하철 노선 수정")
	@Test
	void updateStationLine() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("수유역", "강변역"));
		final Long lineId = AcceptanceUtils.createStationLine("4호선", "blue", stationIds.get(0), stationIds.get(1), BigDecimal.TEN);

		//when
		AcceptanceUtils.updateStationLine(lineId, "9호선", "brown");

		//then
		final JsonPath jsonPath = AcceptanceUtils.getStationLine(lineId);
		Assertions.assertEquals("9호선", jsonPath.getString("name"));
		Assertions.assertEquals("brown", jsonPath.getString("color"));
	}

	/**
	 * Given 수유역,강변역을 생성한다
	 * Given 수유역,강변역으로 4번 노선을 생성한다
	 * WHEN 4번 노선을 삭제한다
	 * Then 지하철 노선목록 조회 시 4번 노선이 조회되지 않는다
	 */
	@DisplayName("지하철 노선 삭제")
	@Test
	void deleteStationLine() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("수유역", "강변역"));
		final Long lineId = AcceptanceUtils.createStationLine("4호선", "blue", stationIds.get(0), stationIds.get(1), BigDecimal.TEN);

		//when
		AcceptanceUtils.deleteStationLine(lineId);

		//then
		final List<Long> lineIds = AcceptanceUtils.getStationLines().getList("id", Long.class);

		Assertions.assertFalse(lineIds.contains(lineId));
	}
}
