package subway.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import subway.fixture.line.LineEntityFixture;
import subway.fixture.line.LineRequestFixture;
import subway.fixture.section.SectionEntityFixture;
import subway.fixture.station.StationEntityFixture;
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
		Line 신분당선 = LineEntityFixture.신분당선_1구간_추가();
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(신분당선));

		// when
		Line actualLine = lineService.findLineById(anyLong());

		// then
		assertThat(actualLine).isEqualTo(신분당선);
	}

	@DisplayName("저장된 모든 노선을 조회한다.")
	@Test
	void successLines() {
		// given
		List<Line> 모든_노선_리스트 = LineEntityFixture.모든_노선_리스트();
		given(lineRepository.findAll()).willReturn(모든_노선_리스트);

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
		Line 신분당선 = LineEntityFixture.신분당선();
		given(lineRepository.save(any(Line.class))).willReturn(신분당선);

		// when
		Line 신분당선1 = LineEntityFixture.신분당선();
		Station 강남역 = StationEntityFixture.강남역();
		Station 양재역 = StationEntityFixture.양재역();
		Line savedLine = lineService.save(신분당선1, 강남역, 양재역, SectionEntityFixture.DISTANCE);

		// then
		assertThat(savedLine).usingRecursiveComparison().isEqualTo(신분당선);
	}

	@DisplayName("노선의 이름을 수정한다.")
	@Test
	void successUpdateName() {
		// given
		String changeName = "변경된 이름";
		Line 신분당선 = LineEntityFixture.신분당선();
		LineUpdateRequest lineUpdateRequest =
			LineRequestFixture.updateBuilder()
				.name(changeName)
				.build();

		// when
		lineService.update(신분당선, lineUpdateRequest);

		// then
		assertThat(신분당선.getName()).isEqualTo(changeName);
	}

	@DisplayName("노선의 색깔의 수정한다.")
	@Test
	void successUpdateColor() {
		// given
		String changeColor = "변경된 색깔";
		Line 신분당선 = LineEntityFixture.신분당선();
		LineUpdateRequest lineUpdateRequest =
			LineRequestFixture.updateBuilder()
				.color(changeColor)
				.build();

		// when
		lineService.update(신분당선, lineUpdateRequest);

		// then
		assertThat(신분당선.getColor()).isEqualTo(changeColor);
	}

	@DisplayName("노선을 삭제한다.")
	@Test
	void successDelete() {
		// given
		Line 신분당선_1구간_추가 = LineEntityFixture.신분당선_1구간_추가();

		// when
		lineService.delete(신분당선_1구간_추가.getId());

		// then
		verify(lineRepository).deleteById(신분당선_1구간_추가.getId());
	}

	@DisplayName("노선에 구간을 추가한다.")
	@Test
	void successAddSection() {
		// given
		Line 신분당선_1구간_추가 = LineEntityFixture.신분당선_1구간_추가();
		Station 양재역 = StationEntityFixture.양재역();
		Station 논현역 = StationEntityFixture.논현역();
		Integer distance = SectionEntityFixture.DISTANCE;

		// when
		lineService.addSection(신분당선_1구간_추가, 양재역, 논현역, distance);

		// then
		Line 신분당선_2구간_추가 = LineEntityFixture.신분당선_2구간_추가();
		assertThat(신분당선_1구간_추가).usingRecursiveComparison().isEqualTo(신분당선_2구간_추가);
	}

	@DisplayName("노선에 구간을 삭제한다.")
	@Test
	void successDeleteSection() {
		// given
		Line 신분당선_2구간_추가 = LineEntityFixture.신분당선_2구간_추가();
		Station 논현역 = StationEntityFixture.논현역();

		// when
		lineService.deleteSection(신분당선_2구간_추가, 논현역);

		// then
		Line 신분당선_1구간_추가 = LineEntityFixture.신분당선_1구간_추가();
		assertThat(신분당선_2구간_추가).usingRecursiveComparison().isEqualTo(신분당선_1구간_추가);
	}
}
