package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Mock을 활용한 LineServiceTest")
@ExtendWith(MockitoExtension.class)
@Transactional
public class LineServiceMockTest {

	public static final long 신분당선 = 1L;
	public static final long 광교역 = 2L;
	public static final long 광교중앙역 = 3L;

	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;

	@InjectMocks
	private LineService lineService;

	@Test
	void addSection() {
		// given
		when(lineRepository.findById(신분당선)).thenReturn(Optional.of(new Line("신분당선", "red")));
		when(stationService.findById(광교중앙역)).thenReturn(new Station("광교중앙역"));
		when(stationService.findById(광교역)).thenReturn(new Station("광교역"));

		// when
		lineService.addSection(신분당선, new SectionRequest(광교역, 광교중앙역, 10));

		// then
		LineResponse 신분당선_응답 = lineService.findById(신분당선);
		assertAll(
				() -> assertThat(신분당선_응답.getName()).isEqualTo("신분당선"),
				() -> assertThat(신분당선_응답.getStations()).hasSize(2)
        );
	}

	@Test
	void deleteSection() {
		//given
		when(lineRepository.findById(신분당선)).thenReturn(Optional.of(new Line("신분당선", "red")));
		when(stationService.findById(광교중앙역)).thenReturn(new Station("광교중앙역"));
		when(stationService.findById(광교역)).thenReturn(new Station("광교역"));

		//when
		lineService.addSection(신분당선, new SectionRequest(광교역, 광교중앙역, 10));
		lineService.deleteSection(신분당선, 광교중앙역);

		//then
		LineResponse 신분당선_응답 = lineService.findById(신분당선);
		assertThat(신분당선_응답.getStations()).isEmpty();
	}

	@Test
	void saveLine() {
		//given
		when(lineRepository.save(any(Line.class))).thenReturn(new Line("신분당선", "red"));

		//when
		LineResponse 신분당선_응답 = lineService.saveLine(new LineRequest("신분당선", "red", 광교역, 광교중앙역, 10));

		//then
		assertAll(
				() -> assertThat(신분당선_응답.getName()).isEqualTo("신분당선"),
				() -> assertThat(신분당선_응답.getStations()).hasSize(2)
		);
	}

	@Test
	void showLines() {
		//given
		when(lineRepository.findAll()).thenReturn(List.of(new Line("신분당선", "red")));

		//when
		List<LineResponse> 노선_응답 = lineService.showLines();

		//then
		assertAll(
				() -> assertThat(노선_응답).hasSize(1),
				() -> assertThat(노선_응답.get(0).getName()).isEqualTo("신분당선")
		);
	}

	@Test
	void findById() {
		//given
		when(lineRepository.findById(신분당선)).thenReturn(Optional.of(new Line("신분당선", "red")));

		//when
		LineResponse 신분당선_응답 = lineService.findById(신분당선);

		//then
		assertThat(신분당선_응답.getName()).isEqualTo("신분당선");
	}
}
