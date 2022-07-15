package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;

	@Mock
	private StationRepository stationRepository;

	private static String LINE2 = "2호선";
	private static String LINE2_COLOR = "2호선";

	@Test
	void addSection() {
		long lineId = 1;
		// given
		// lineRepository, stationService stub 설정을 통해 초기값 셋팅
		when(lineRepository.findById(any()))
			.thenReturn(Optional.of(new Line(LINE2, LINE2_COLOR)));
		LineService lineService = new LineService(lineRepository, stationService, stationRepository);

		// when
		// lineService.addSection 호출
		lineService.addSection(lineId, new SectionRequest(1L, 2L, 3));

		// then
		// line.findLineById 메서드를 통해 검증
		Line resultLine = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
		assertThat(resultLine.getSections()).hasSize(1);

	}
}
