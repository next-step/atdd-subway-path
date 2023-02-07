package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.SectionFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.exception.LineErrorCode;
import nextstep.subway.domain.exception.StationErrorCode;
import nextstep.subway.domain.exception.StationNotFoundException;
import nextstep.subway.domain.exception.SubwayBadRequestException;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;

	@InjectMocks
	private LineService lineService;

	@Test
	void addSection() throws Exception {
		// given
		when(lineRepository.findById(LINE_4_ID))
			.thenReturn(Optional.of(LINE_4()));

		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(withId(동대문역사문화공원, 동대문역사문화공원_ID));
		when(stationService.findById(충무로_ID)).thenReturn(withId(충무로, 충무로_ID));

		// when
		lineService.addSection(LINE_4_ID, 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 10));
		Line line = lineRepository.findById(LINE_4_ID).get();

		// then
		assertThat(line.getSections()).hasSize(2);
	}

	@DisplayName("상행 하행 종점역중 등록되지 않은 역을 요청할 경우 예외가 발생한다")
	@Test
	void 상행_하행_종점역중_등록되지_않은_역을_요청할_경우_예외가_발생한다() throws Exception {
		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(withId(동대문역사문화공원, 동대문역사문화공원_ID));
		when(stationService.findById(등록되지않은_역_ID))
			.thenThrow(new StationNotFoundException(StationErrorCode.NOT_FOUND_STATION));
		SectionRequest 동대문역사문화공원_등록되지않은역 = 구간_추가_요청(동대문역사문화공원_ID, 등록되지않은_역_ID, 5);

		assertThatThrownBy(() -> lineService.addSection(LINE_4_ID, 동대문역사문화공원_등록되지않은역))
			.isInstanceOf(StationNotFoundException.class)
			.hasMessage(StationErrorCode.NOT_FOUND_STATION.getMessage());
	}

	@DisplayName("노선생성시 구간거리를 0으로 요청할 경우 예외가 발생한다")
	@Test
	void 노선생성시_구간거리를_잘못요청할_경우_예외가_발생한다() throws Exception {
		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(withId(동대문역사문화공원, 동대문역사문화공원_ID));
		when(stationService.findById(충무로_ID)).thenReturn(withId(충무로, 충무로_ID));

		LineRequest lineRequest = new LineRequest("4호선", "blue", 동대문역사문화공원_ID, 충무로_ID, 0);

		assertThatThrownBy(() -> lineService.saveLine(lineRequest))
			.isInstanceOf(SubwayBadRequestException.class)
			.hasMessage(LineErrorCode.INVALID_SECTION_DISTANCE.getMessage());
	}

	@Test
	void getStations() throws Exception {
		// given
		when(lineRepository.findById(LINE_4_ID))
			.thenReturn(Optional.of(LINE_4()));

		when(stationService.findById(동대문역사문화공원_ID)).thenReturn(withId(동대문역사문화공원, 동대문역사문화공원_ID));
		when(stationService.findById(충무로_ID)).thenReturn(withId(충무로, 충무로_ID));
		lineService.addSection(LINE_4_ID, 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5));

		// when
		LineResponse lineResponse = lineService.findById(LINE_4_ID);
		List<StationResponse> stationResponses = lineResponse.getStations();

		// then
		assertThat(stationResponses).hasSize(3);
	}
}
