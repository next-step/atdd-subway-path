package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import nextstep.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
	private static final Long LINE_ID = 1L;
	private static final Long 강남역_ID = 1L;
	private static final Long 선릉역_ID = 2L;
	private static final Long 역삼역_ID = 3L;

	private SectionRequest 강남역_선릉역_구간_생성_요청_데이터;
	private SectionRequest 선릉역_역삼역_구간_생성_요청_데이터;

	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;
	@InjectMocks
	private LineService lineService;

	@BeforeEach
	void setUp() {
		StubOfStationFindById("강남역", 강남역_ID);
		StubOfStationFindById("선릉역", 선릉역_ID);
		StubOfLineFindById("이호선", "green", LINE_ID);

		강남역_선릉역_구간_생성_요청_데이터 = new SectionRequest(강남역_ID, 선릉역_ID, 10);
		선릉역_역삼역_구간_생성_요청_데이터 = new SectionRequest(선릉역_ID, 역삼역_ID, 10);
	}

	@DisplayName("구간 추가")
	@Test
	void addSection() {
		// when
		lineService.addSection(LINE_ID, 강남역_선릉역_구간_생성_요청_데이터);
		// then
		Line line = lineService.findLineById(LINE_ID);
		assertThat(line.getSections()).isNotEmpty();
	}

	@DisplayName("노선의 구간 삭제")
	@Test
	void deleteSection() {
		//given
		StubOfStationFindById("역삼역", 역삼역_ID);
		lineService.addSection(LINE_ID, 강남역_선릉역_구간_생성_요청_데이터);
		lineService.addSection(LINE_ID, 선릉역_역삼역_구간_생성_요청_데이터);

		// when
		lineService.deleteSection(LINE_ID, 역삼역_ID);

		// then
		Line line = lineService.findLineById(LINE_ID);
		assertThat(line.getSections()).hasSize(1);
	}

	void StubOfLineFindById(String name, String color, Long id) {
		when(lineRepository.findById(id)).thenReturn(Optional.of(new Line(name, color)));
	}

	void StubOfStationFindById(String name, Long id) {
		when(stationService.findById(id)).thenReturn(new Station(name));
	}
}
