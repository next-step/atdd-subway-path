package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;

	@InjectMocks
	private LineService lineService;

	@Test
	void addSection() {
		// given
		when(lineRepository.findById(LINE_4_ID))
			.thenReturn(Optional.of(LINE_4));

		when(stationService.findById(동대문_ID)).thenReturn(동대문);
		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(동대문역사문화공원);

		// when
		lineService.addSection(LINE_4_ID, new SectionRequest(동대문_ID, 동대문역사문화공원_ID, 10));

		// then
		Line line = lineRepository.findById(LINE_4_ID).get();
		assertThat(line.getSections()).hasSize(1);
	}
}
