package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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

	@Test
	void addSection() {
		// given
		LineService lineService = new LineService(lineRepository, stationService);
		when(lineRepository.findById(신분당선)).thenReturn(Optional.of(new Line("신분당선", "red")));
		when(stationService.findById(광교역)).thenReturn(new Station("광교역"));
		when(stationService.findById(광교중앙역)).thenReturn(new Station("광교중앙역"));

		// when
		lineService.addSection(신분당선, new SectionRequest(광교역, 광교중앙역, 10));

		// then
		LineResponse 신분당선_응답 = lineService.findById(1L);
		assertAll(
				() -> assertThat(신분당선_응답.getName()).isEqualTo("신분당선"),
				() -> assertThat(신분당선_응답.getStations()).hasSize(2)
        );
	}

	@Test
	void deleteSection() {
		//given
		LineService lineService = new LineService(lineRepository, stationService);
		when(lineRepository.findById(신분당선)).thenReturn(Optional.of(new Line("신분당선", "red")));
		when(stationService.findById(광교역)).thenReturn(new Station("광교역"));
		when(stationService.findById(광교중앙역)).thenReturn(new Station("광교중앙역"));

		//when
		lineService.addSection(신분당선, new SectionRequest(광교역, 광교중앙역, 10));
		lineService.deleteSection(신분당선, 광교중앙역);

		//then
		LineResponse 신분당선_응답 = lineService.findById(신분당선);
		assertThat(신분당선_응답.getStations()).isEmpty();
	}
}
