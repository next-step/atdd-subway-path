package nextstep.subway.unit;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
	@Mock
	LineRepository lineRepository;
	@Mock
	StationRepository stationRepository;

	@Test
	void getPaths() {
		List<Line> lineList = Arrays.asList(
			new Line(1L, "2호선", "green"),
			new Line(2L, "3호선", "green"),
			new Line(3L, "신분당선", "green"));

		when(lineRepository.findAll())
			.thenReturn(lineList);
		when(lineList.get(0).getSections())
			.thenReturn(null);
		when(stationRepository.findById(1L))
			.thenReturn(Optional.of(new Station(1L, "교대약")));
		when(stationRepository.findById(2L))
			.thenReturn(Optional.of(new Station(2L, "남부터미널역")));
		when(stationRepository.findById(3L))
			.thenReturn(Optional.of(new Station(3L, "양재역")));
		when(stationRepository.findById(4L))
			.thenReturn(Optional.of(new Station(4L, "양재역")));
	}
}
