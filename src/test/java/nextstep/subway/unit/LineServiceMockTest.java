package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.SectionFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.SectionFixtures;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

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
		lineService.addSection(LINE_4_ID, 구간_추가_요청(동대문_ID, 동대문역사문화공원_ID, 10));
		Line line = lineRepository.findById(LINE_4_ID).get();

		// then
		assertThat(line.getSections()).hasSize(1);
	}

	@Test
	void getStations() throws Exception {
		// given
		when(lineRepository.findById(LINE_4_ID))
			.thenReturn(Optional.of(LINE_4));

		when(stationService.findById(동대문_ID)).thenReturn(withId(동대문, 동대문_ID));
		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(withId(동대문역사문화공원, 동대문역사문화공원_ID));
		when(stationService.findById(충무로_ID)).thenReturn(withId(충무로, 충무로_ID));

		lineService.addSection(LINE_4_ID, 구간_추가_요청(동대문_ID, 동대문역사문화공원_ID, 10));
		lineService.addSection(LINE_4_ID, 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5));


		// when
		LineResponse lineResponse = lineService.findById(LINE_4_ID);
		List<StationResponse> stationResponses = lineResponse.getStations();

		// then
		assertThat(stationResponses).hasSize(3);
	}
}
