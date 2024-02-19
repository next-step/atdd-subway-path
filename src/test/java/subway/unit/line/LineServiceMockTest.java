package subway.unit.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static subway.fixture.line.LineEntityFixture.*;
import static subway.fixture.station.StationEntityFixture.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.fixture.line.LineRequestFixture;
import subway.line.Line;
import subway.line.LineRepository;
import subway.line.LineService;
import subway.line.Section;
import subway.station.Station;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
	@InjectMocks
	private LineService lineService;

	@Mock
	private LineRepository lineRepository;

	@DisplayName("입력 받은 ID로 노선을 조회한다.")
	@Test
	void successFindById() {
		// given
		Line 신분당선 = 신분당선_1구간_추가();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선));

		// when
		LineResponse actualLine = lineService.findLineById(anyLong());

		// then
		assertThat(actualLine).usingRecursiveComparison().isEqualTo(LineResponse.of(신분당선));
	}

	@DisplayName("저장된 모든 노선을 조회한다.")
	@Test
	void successLines() {
		// given
		List<Line> 모든_노선_리스트 = 모든_노선_리스트();
		given(lineRepository.findAll()).willReturn(모든_노선_리스트());

		// when
		List<LineResponse> actualResponse = lineService.lines();

		// then
		List<LineResponse> expectedResponse = 모든_노선_리스트.stream()
			.map(LineResponse::of)
			.collect(toList());
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	@DisplayName("노선을 등록한다.")
	@Test
	void successSave() {
		// given
		Line 신분당선 = 신분당선();
		given(lineRepository.save(any(Line.class))).willReturn(신분당선);

		// when
		Station 강남역 = 강남역();
		Station 양재역 = 양재역();
		LineResponse savedLine = lineService.save(신분당선, 강남역, 양재역, DISTANCE);

		// then
		Line 신분당선1 = 신분당선();
		신분당선1.addSection(new Section(신분당선1, 강남역, 양재역, DISTANCE));
		assertThat(savedLine).usingRecursiveComparison().isEqualTo(LineResponse.of(신분당선1));
	}

	@DisplayName("노선의 이름을 수정한다.")
	@Test
	void successUpdateName() {
		// given
		String changeName = "변경된 이름";

		Line 신분당선 = 신분당선_1구간_추가();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선));

		LineUpdateRequest lineUpdateRequest =
			LineRequestFixture.lineUpdateRequest()
				.name(changeName)
				.build();

		// when
		lineService.update(신분당선.getId(), lineUpdateRequest);

		// then
		assertThat(신분당선.getName()).isEqualTo(changeName);
	}

	@DisplayName("노선의 색깔의 수정한다.")
	@Test
	void successUpdateColor() {
		// given
		String changeColor = "변경된 색깔";
		Line 신분당선 = 신분당선_1구간_추가();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선));

		LineUpdateRequest lineUpdateRequest =
			LineRequestFixture.lineUpdateRequest()
				.color(changeColor)
				.build();

		// when
		lineService.update(신분당선.getId(), lineUpdateRequest);

		// then
		assertThat(신분당선.getColor()).isEqualTo(changeColor);
	}

	@DisplayName("노선을 삭제한다.")
	@Test
	void successDelete() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();

		// when
		lineService.delete(신분당선_1구간_추가.getId());

		// then
		verify(lineRepository).deleteById(신분당선_1구간_추가.getId());
	}

	@DisplayName("노선의 최초 정류장 앞에 구간을 추가한다.")
	@Test
	void successAddBeforeFirstSection() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		Station 강남역 = 강남역();
		Station 논현역 = 논현역();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선_1구간_추가));

		// when
		LineResponse lineResponse = lineService.addSection(신분당선_1구간_추가.getId(), 논현역, 강남역, DISTANCE);

		// then
		assertThat(lineResponse).usingRecursiveComparison().isEqualTo(lineResponse);
	}

	@DisplayName("노선의 중간 지점에 구간을 추가한다.")
	@Test
	void successAddMiddleSection() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		Station 강남역 = 강남역();
		Station 논현역 = 논현역();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선_1구간_추가));

		// when
		LineResponse lineResponse = lineService.addSection(신분당선_1구간_추가.getId(), 강남역, 논현역, 8);

		// then
		assertThat(lineResponse).usingRecursiveComparison().isEqualTo(lineResponse);
	}

	@DisplayName("노선의 마지막 정류장에 이어서 구간을 추가한다.")
	@Test
	void successAddAfterLastSection() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		Station 양재역 = 양재역();
		Station 논현역 = 논현역();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선_1구간_추가));

		// when
		LineResponse lineResponse = lineService.addSection(신분당선_1구간_추가.getId(), 양재역, 논현역, DISTANCE);

		// then
		assertThat(lineResponse).usingRecursiveComparison().isEqualTo(lineResponse);
	}

	@DisplayName("노선의 마지막 정류장에 이어서 구간을 추가한다.")
	@Test
	void failAddSection() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		Station 양재역 = 양재역();
		Station 논현역 = 논현역();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선_1구간_추가));

		// when
		LineResponse lineResponse = lineService.addSection(신분당선_1구간_추가.getId(), 양재역, 논현역, DISTANCE);

		// then
		assertThat(lineResponse).usingRecursiveComparison().isEqualTo(lineResponse);
	}

	@DisplayName("노선에 구간을 삭제한다.")
	@Test
	void successDeleteSection() {
		// given
		Line 신분당선 = 신분당선_2구간_추가();
		Station 논현역 = 논현역();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선));

		// when
		lineService.deleteSection(신분당선.getId(), 논현역);

		// then
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(신분당선_1구간_추가);
	}
}
