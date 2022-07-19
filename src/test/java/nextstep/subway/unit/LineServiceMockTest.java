package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
	@Mock
	private LineRepository lineRepository;
	@Mock
	private StationService stationService;
	@InjectMocks
	private LineService lineService;

	private Station upStation;
	private Station downStation;
	private Line line;

	@BeforeEach
	void setUp() {
		upStation = new Station("서현역");
		downStation = new Station("이매역");
		line = new Line("분당선", "yellow");
	}

	@Test
	void addSection() {
		// given
		// lineRepository, stationService stub 설정을 통해 초기값 셋팅
		given(stationService.findById(1L)).willReturn(upStation);
		given(stationService.findById(2L)).willReturn(downStation);
		given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
		SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);

		// when
		// lineService.addSection 호출
		lineService.addSection(1L, sectionRequest);

		// then
		// line.findLineById 메서드를 통해 검증
		verify(lineRepository).findById(1L);
		assertThat(line.getSections()).hasSize(1);
	}
}
