package subway;

import org.junit.jupiter.api.Assertions;
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

	/**
	 * Given 지하철 역 A,C 와 해당역이 포함된 지하철 노선을 생성한다
	 * Given 지하철 역 B를 생성한다
	 * When 지하철 노선에 (C,B) 구간을 추가한다
	 * Then 지하철 노선 조회 시 추가한 구간의 역이 목록에 포함된다
	 * Then 지하철 노선의 하행 종점역은 새로 추가된 B역이 된다
	 */
	@DisplayName("정상적인 지하철 구간 등록")
	@Test
	void createStationLineSection() {
		//given
		final Long aStationId = AcceptanceUtils.createStation("A역");
		final Long cStationId = AcceptanceUtils.createStation("C역");
		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, cStationId, BigDecimal.TEN);

		final Long bStationId = AcceptanceUtils.createStation("B역");

		//when
		AcceptanceUtils.createStationLineSection(lineId, cStationId, bStationId, BigDecimal.ONE, HttpStatus.OK);

		//then
		final List<String> stationNames = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);

		Assertions.assertEquals("A역", stationNames.get(0));
		Assertions.assertEquals("C역", stationNames.get(1));
		Assertions.assertEquals("B역", stationNames.get(2));
	}

	/**
	 * Given 지하철 역 A,B,C,D,E를 생성한다
	 * Given 지하철 역 A,B로 노선을 생성한다.
	 * Given 지하철 노선에 (B,C), (C,D) 구간을 추가한다
	 * When 지하철 노선에 (A,E) 구간을 추가한다
	 * Then 에러 발생
	 */
	@DisplayName("새로운 구간의 상행역이 노선의 하행 종점역이 아닌 경우 구간 생성 시 에러")
	@Test
	void createStationSection_upStation_notEqual_lineLastDownStation() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역", "C역", "D역", "E역"));

		final Long aStationId = stationIds.get(0);
		final Long bStationId = stationIds.get(1);
		final Long cStationId = stationIds.get(2);
		final Long dStationId = stationIds.get(3);
		final Long eStationId = stationIds.get(4);

		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

		AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.ONE);
		AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.ONE);

		//when & then
		AcceptanceUtils.createStationLineSection(lineId, aStationId, eStationId, BigDecimal.ONE, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 역 A,B,C을 생성한다
	 * Given 지하철 역 A,B로 노선을 생성한다
	 * Given 지하철 노선에 (B,C) 구간을 추가한다
	 * When 지하철 노선에 (A,C) 구간을 추가한다
	 * Then 에러 발생
	 */
	@DisplayName("새로운 구간의 하행역이 이미 노선에 등록된 역으로 구간생성 시 에러")
	@Test
	void createStationSection_sectionDownStation_registeredTo_lineStation() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역", "C역"));

		final Long aStationId = stationIds.get(0);
		final Long bStationId = stationIds.get(1);
		final Long cStationId = stationIds.get(2);

		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

		//when
		AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.ONE, HttpStatus.OK);

		//then
		AcceptanceUtils.createStationLineSection(lineId, aStationId, cStationId, BigDecimal.ONE, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 역 A,B,C,D를 생성한다
	 * Given 지하철 역 A,B로 노선을 생성한다
	 * Given 지하철 노선에 (B,C) (C,D) 구간을 추가한다
	 * When 지하철 노선에서 D역을 제거한다
	 * Then 지하철 노선 조회시 역이름 목록에서 D가 조회되지 않는다
	 */
	@DisplayName("정상적인 지하철 구간 제거")
	@Test
	void deleteStationLineSection() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역", "C역", "D역"));
		final Long aStationId = stationIds.get(0);
		final Long bStationId = stationIds.get(1);
		final Long cStationId = stationIds.get(2);
		final Long dStationId = stationIds.get(3);

		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

		AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.ONE);
		AcceptanceUtils.createStationLineSection(lineId, cStationId, dStationId, BigDecimal.ONE);

		//when
		AcceptanceUtils.deleteStationLineSection(lineId, dStationId, HttpStatus.OK);

		//then
		final List<String> names = AcceptanceUtils.getStationLine(lineId).getList("stations.name", String.class);

		Assertions.assertEquals(3, names.size());
		Assertions.assertFalse(names.contains("D역"));
	}

	/**
	 * Given 지하철 역 A,B,C를 생성한다
	 * Given 지하철 역 A,B로 노선을 생성한다
	 * Given 지하철 노선에 (B,C) 구간을 추가한다
	 * When 지하철 노선에서 B역을 제거한다
	 * Then 에러 발생
	 */
	@DisplayName("하행 종점역이 아닌 지하철역 구간 제거시 에러")
	@Test
	void deleteStationLineSection_NotLastDownStation() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역", "C역"));
		final Long aStationId = stationIds.get(0);
		final Long bStationId = stationIds.get(1);
		final Long cStationId = stationIds.get(2);

		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

		AcceptanceUtils.createStationLineSection(lineId, bStationId, cStationId, BigDecimal.ONE);

		//when & then
		AcceptanceUtils.deleteStationLineSection(lineId, bStationId, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 역 A,B를 생성한다
	 * Given 지하철 역 A,B로 노선을 생성한다
	 * When 지하철 노선에서 B역을 제거한다
	 * Then 에러 발생
	 */
	@DisplayName("2개의 역으로 이뤄진 지하철역의 구간제거시 에러")
	@Test
	void deleteStationLineSection_hasOnly2StationLine() {
		//given
		final List<Long> stationIds = AcceptanceUtils.createStations(List.of("A역", "B역"));
		final Long aStationId = stationIds.get(0);
		final Long bStationId = stationIds.get(1);

		final Long lineId = AcceptanceUtils.createStationLine("1호선", "blue", aStationId, bStationId, BigDecimal.TEN);

		//when & then
		AcceptanceUtils.deleteStationLineSection(lineId, bStationId, HttpStatus.BAD_REQUEST);
	}
}
