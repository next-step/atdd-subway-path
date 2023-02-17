package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import nextstep.subway.line.LineCreateRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.LineUpdateRequest;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

	@Autowired private LineRepository lineRepository;
	@Autowired private StationRepository stationRepository;

	@Autowired private LineService lineService;

	private Long line4;
	private Station 사당역;
	private Station 금정역;

	public LineServiceTest() {
	}

	@BeforeEach
	void setup() {
		사당역 = new Station("사당역");
		금정역 = new Station("금정역");
		stationRepository.save(사당역);
		stationRepository.save(금정역);

		line4 = lineService.saveLine(new LineCreateRequest("4호선", "#00A5DE", 사당역.getId(), 금정역.getId(), 10)).getId();
	}

	@DisplayName("지하철 노선을 등록할 수 있다")
	@Test
	void saveLineTest() {
		// when lineService.addSection 호출
		Long saveLineId = lineService.saveLine(new LineCreateRequest("TEST_LINE", "BLACK", 사당역.getId(), 금정역.getId(), 10)).getId();

		// then line.findLineById 메서드를 통해 검증
		LineResponse lineResponse = lineService.findLineById(saveLineId);
		assertAll(
			() -> assertThat(lineResponse.getId()).isEqualTo(saveLineId),
			() -> assertThat(lineResponse.getName()).isEqualTo("TEST_LINE"),
			() -> assertThat(lineResponse.getColor()).isEqualTo("BLACK"),
			() -> assertThat(lineResponse.getStations()).hasSize(2)
		);
	}

	@DisplayName("등록된 지하철 노선 목록을 조회할 수 있다")
	@Test
	void showLinesTest() {
		// given
		lineService.saveLine(new LineCreateRequest("2호선", "#00A5DE", 사당역.getId(), 금정역.getId(), 10)).getId();

		// when
		List<LineResponse> lineResponses = lineService.showLines();

		// then
		assertThat(lineResponses).hasSize(2);
	}

	@DisplayName("등록된 지하철 노선의 정보를 수정할 수 있다")
	@Test
	void updateLineByIdTest() {
		// when
		lineService.updateLineById(line4, new LineUpdateRequest("2호선", "#00A5DE"));

		// then line.findLineById 메서드를 통해 검증
		LineResponse lineResponse = lineService.findLineById(line4);
		assertAll(
			() -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
			() -> assertThat(lineResponse.getColor()).isEqualTo("#00A5DE")
		);
	}

	@DisplayName("등록된 지하철 노선을 삭제할 수 있다")
	@Test
	void deleteLineByIdTest() {
		// when
		lineService.deleteLineById(line4);

		// then
		assertThrows(IllegalArgumentException.class, () -> lineService.findLineById(line4));
	}
}
