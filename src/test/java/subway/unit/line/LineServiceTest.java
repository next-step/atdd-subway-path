package subway.unit.line;

import static org.assertj.core.api.Assertions.*;
import static subway.fixture.line.LineEntityFixture.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.station.StationRequest;
import subway.dto.station.StationResponse;
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
		LineResponse 신분당선 = 신분당선_생성();

		// then
		LineResponse expectedLine = lineService.findLineById(신분당선.getId());
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(expectedLine);
	}

	@DisplayName("노선의 이름을 변경한다.")
	@Test
	void successChangeLineName() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture.lineUpdateRequest().build();

		// when
		lineService.update(신분당선.getId(), request);

		// then
		LineResponse expectedResponse = lineService.findLineById(신분당선.getId());
		assertThat(request.getName()).isEqualTo(expectedResponse.getName());
	}

	@DisplayName("노선의 이름은 20자를 넘길수 없다.")
	@Test
	void failChangeLineName() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture
			.lineUpdateRequest()
			.name(MORE_THAN_20_CHARACTERS)
			.build();

		// then
		Long lineId = 신분당선.getId();
		assertThatThrownBy(() -> lineService.update(lineId, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 이름은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 색깔을 변경한다.")
	@Test
	void successChangeLineColor() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture.lineUpdateRequest().build();

		// when
		lineService.update(신분당선.getId(), request);

		// then
		LineResponse expectedResponse = lineService.findLineById(신분당선.getId());
		assertThat(request.getColor()).isEqualTo(expectedResponse.getColor());
	}

	@DisplayName("노선의 색깔은 20자를 넘길수 없다.")
	@Test
	void failChangeLineColor() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		LineUpdateRequest request = LineRequestFixture
			.lineUpdateRequest()
			.color(MORE_THAN_20_CHARACTERS)
			.build();

		// then
		Long lineId = 신분당선.getId();
		assertThatThrownBy(() -> lineService.update(lineId, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 색깔은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("등록된 노선을 조회한다.")
	@Test
	void successFindById() {
		// given
		LineResponse 신분당선 = 신분당선_생성();

		// when
		LineResponse expectedLine = lineService.findLineById(신분당선.getId());

		// then
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(expectedLine);
	}

	@DisplayName("노선의 최초 정류장 앞에 구간을 추가한다.")
	@Test
	void successAddBeforeFirstSection() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		Station 첫번째_정류장 = 정류장_조회(신분당선.getStations().get(0).getId());
		Station 신논현역 = 정류장_생성("신논현역");

		// when
		LineResponse actualResponse = lineService.addSection(신분당선.getId(), 신논현역, 첫번째_정류장, 10);

		// then
		LineResponse expectedResponse = lineService.findLineById(신분당선.getId());
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	@DisplayName("노선의 중간 지점에 구간을 추가한다.")
	@Test
	void successAddMiddleSection() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		Station 첫번째_정류장 = 정류장_조회(신분당선.getStations().get(0).getId());
		Station 신논현역 = 정류장_생성("신논현역");

		// when
		LineResponse actualResponse = lineService.addSection(신분당선.getId(), 첫번째_정류장, 신논현역, 8);

		// then
		LineResponse expectedResponse = lineService.findLineById(신분당선.getId());
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	@DisplayName("노선의 마지막 정류장에 이어서 구간을 추가한다.")
	@Test
	void successAddAfterLastSection() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		List<StationResponse> stations = 신분당선.getStations();
		Station 마지막_정류장 = 정류장_조회(stations.get(stations.size() - 1).getId());
		Station 신논현역 = 정류장_생성("신논현역");

		// when
		LineResponse actualResponse = lineService.addSection(신분당선.getId(), 마지막_정류장, 신논현역, 10);

		// then
		LineResponse expectedResponse = lineService.findLineById(신분당선.getId());
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	@DisplayName("노선의 마지막 정류장에 이어서 구간을 추가 하는 경우, 이미 등록된 정류장은 사용할 수 없다.")
	@Test
	void failAddSection1() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		List<StationResponse> stations = 신분당선.getStations();
		Station 마지막_정류장 = 정류장_조회(stations.get(stations.size() - 1).getId());
		Station 강남역 = 정류장_생성("강남역");

		// then
		Long lineId = 신분당선.getId();
		assertThatThrownBy(() -> lineService.addSection(lineId, 마지막_정류장, 강남역, 10))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
	}

	@DisplayName("노선의 중간 지점에 구간을 추가 하는 경우, 이미 등록된 정류장은 사용할 수 없다.")
	@Test
	void failAddSection2() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		Station 첫번째_정류장 = 정류장_조회(신분당선.getStations().get(0).getId());
		Station 양재역 = 정류장_생성("양재역");

		// then
		Long lineId = 신분당선.getId();
		assertThatThrownBy(() -> lineService.addSection(lineId, 첫번째_정류장, 양재역, 10))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
	}

	@DisplayName("노선의 최초 정류장 앞에 구간을 추가 하는 경우, 이미 등록된 정류장은 사용할 수 없다.")
	@Test
	void failAddSection3() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		Station 첫번째_정류장 = 정류장_조회(신분당선.getStations().get(0).getId());
		Station 양재역 = 정류장_생성("양재역");

		// then
		Long id = 신분당선.getId();
		assertThatThrownBy(() -> lineService.addSection(id, 양재역, 첫번째_정류장, 10))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
	}

	@DisplayName("노선의 구간을 삭제한다.")
	@Test
	void successDeleteSection() {
		// given
		LineResponse 신분당선 = 신분당선_구간_추가();
		int size = 신분당선.getStations().size();
		Station 마지막_정류장 = 정류장_조회(신분당선.getStations().get(size - 1).getId());

		// when
		lineService.deleteSection(신분당선.getId(), 마지막_정류장);

		// then
		List<LineResponse> lines = lineService.lines();
		assertThat(lines).hasSize(1);
	}

	@DisplayName("모든 노선을 조회한다.")
	@Test
	void successLines() {
		// given
		LineResponse 신분당선 = 신분당선_생성();
		LineResponse 삼호선 = 삼호선_생성();

		// when
		List<LineResponse> actualLineResponse = lineService.lines();

		// then
		List<LineResponse> expectedLineResponse = List.of(신분당선, 삼호선);
		assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(expectedLineResponse);
	}

	private LineResponse 신분당선_생성() {
		Line 신분당선 = 신분당선();
		Station 강남역 = 정류장_생성("강남역");
		Station 양재역 = 정류장_생성("양재역");
		Integer distance = SectionEntityFixture.DISTANCE;

		return lineService.save(신분당선, 강남역, 양재역, distance);
	}

	private LineResponse 신분당선_구간_추가() {
		Line 신분당선 = 신분당선();

		Station 강남역 = 정류장_생성("강남역");
		Station 양재역 = 정류장_생성("양재역");
		Station 논현역 = 정류장_생성("논현역");

		Integer distance = SectionEntityFixture.DISTANCE;

		LineResponse savedLine = lineService.save(신분당선, 강남역, 양재역, distance);

		return lineService.addSection(savedLine.getId(), 양재역, 논현역, distance);
	}

	private LineResponse 삼호선_생성() {
		Line 삼호선 = 삼호선();
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
