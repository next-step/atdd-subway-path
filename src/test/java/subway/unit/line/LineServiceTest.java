package subway.unit.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.station.StationRequest;
import subway.dto.station.StationResponse;
import subway.fixture.line.LineEntityFixture;
import subway.fixture.line.LineRequestFixture;
import subway.fixture.section.SectionEntityFixture;
import subway.line.Line;
import subway.line.LineService;
import subway.station.Station;
import subway.station.StationService;

@Transactional
@SpringBootTest
class LineServiceTest {
	private static final String MORE_THAN_20_CHARACTERS = "012345678901234567891";

	@Autowired
	private LineService lineService;

	@Autowired
	private StationService stationService;

	@DisplayName("노선을 등록한다.")
	@Test
	void successLineSave() {
		// when
		Line 신분당선 = 신분당선_생성();

		// then
		Line expectedLine = lineService.findLineById(신분당선.getId());
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(expectedLine);
	}

	@DisplayName("노선의 이름을 변경한다.")
	@Test
	void successChangeLineName() {
		// given
		Line 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture.updateBuilder().build();

		// when
		lineService.update(신분당선, request);

		// then
		assertThat(신분당선.getName()).isEqualTo(request.getName());
	}

	@DisplayName("노선의 이름은 20자를 넘길수 없다.")
	@Test
	void failChangeLineName() {
		// given
		Line 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture
			.updateBuilder()
			.name(MORE_THAN_20_CHARACTERS)
			.build();

		// then
		assertThatThrownBy(() -> lineService.update(신분당선, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 이름은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 색깔을 변경한다.")
	@Test
	void successChangeLineColor() {
		// given
		Line 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture.updateBuilder().build();

		// when
		lineService.update(신분당선, request);

		// then
		assertThat(신분당선.getColor()).isEqualTo(request.getColor());
	}

	@DisplayName("노선의 색깔은 20자를 넘길수 없다.")
	@Test
	void failChangeLineColor() {
		// given
		Line 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture
			.updateBuilder()
			.color(MORE_THAN_20_CHARACTERS)
			.build();

		// then
		assertThatThrownBy(() -> lineService.update(신분당선, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 색깔은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("등록된 노선을 조회한다.")
	@Test
	void successFindById() {
		// given
		Line 신분당선 = 신분당선_생성();

		// when
		Line expectedLine = lineService.findLineById(신분당선.getId());

		// then
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(expectedLine);
	}

	@DisplayName("노선의 구간을 추가한다.")
	@Test
	void successAddSection() {
		// given
		Line 신분당선 = 신분당선_생성();
		Station 마지막_정류장 = 정류장_조회(신분당선.getSections().getFinalStation().getId());
		Station 신논현역 = 정류장_생성("신논현역");

		// when
		Line actualLine = lineService.addSection(신분당선, 마지막_정류장, 신논현역, 10);

		// then
		Line expectedLine = lineService.findLineById(신분당선.getId());
		assertThat(actualLine).usingRecursiveComparison().isEqualTo(expectedLine);
	}

	@DisplayName("노선의 구간을 삭제한다.")
	@Test
	void successDeleteSection() {
		// given
		Line 신분당선 = 신분당선_구간_추가();
		Station 마지막_정류장 = 정류장_조회(신분당선.getSections().getFinalStation().getId());

		// when
		lineService.deleteSection(신분당선, 마지막_정류장);

		// then
		List<LineResponse> lines = lineService.lines();
		assertThat(lines).hasSize(1);
	}

	@DisplayName("모든 노선을 조회한다.")
	@Test
	void successLines() {
		// given
		Line 신분당선 = 신분당선_생성();
		Line 삼호선 = 삼호선_생성();

		// when
		List<LineResponse> actualLineResponse = lineService.lines();

		// then
		List<LineResponse> expectedLineResponse = Stream.of(신분당선, 삼호선)
			.map(LineResponse::of)
			.collect(toList());

		assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(expectedLineResponse);
	}

	private Line 신분당선_생성() {
		Line 신분당선 = LineEntityFixture.신분당선();
		Station 강남역 = 정류장_생성("강남역");
		Station 양재역 = 정류장_생성("양재역");
		Integer distance = SectionEntityFixture.DISTANCE;

		return lineService.save(신분당선, 강남역, 양재역, distance);
	}

	private Line 신분당선_구간_추가() {
		Line 신분당선 = LineEntityFixture.신분당선();

		Station 강남역 = 정류장_생성("강남역");
		Station 양재역 = 정류장_생성("양재역");
		Station 논현역 = 정류장_생성("논현역");

		Integer distance = SectionEntityFixture.DISTANCE;

		Line line = lineService.save(신분당선, 강남역, 양재역, distance);
		line.addSection(양재역, 논현역, distance);

		return line;
	}

	private Line 삼호선_생성() {
		Line 삼호선 = LineEntityFixture.삼호선();
		Station 불광역 = 정류장_생성("불광역");
		Station 녹번역 = 정류장_생성("녹번역");
		Integer distance = SectionEntityFixture.DISTANCE;

		return lineService.save(삼호선, 불광역, 녹번역, distance);
	}

	private Station 정류장_조회(Long id) {
		return stationService.findStationById(id);
	}

	private Station 정류장_생성(String name) {
		StationResponse stationResponse = stationService.saveStation(new StationRequest(name));
		return stationService.findStationById(stationResponse.getId());
	}
}
