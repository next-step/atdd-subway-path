package nextstep.subway.unit;

import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("경로 탐색 서비스 목 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

	@Mock
	LineQueryService lineQueryService;
	@Mock
	StationService stationService;

	PathService pathService;

	Station 강남역;
	Station 판교역;
	Station 여의도역;
	List<Line> 노선목록;

	Distance 강남_판교_거리;
	Distance 강남_여의도_거리;
	Distance 판교_여의도_거리;

	@BeforeEach
	void setUp() {
		pathService = new PathService(lineQueryService, stationService);

		강남역 = new Station("강남역");
		판교역 = new Station("판교역");
		여의도역 = new Station("여의도역");

		강남_판교_거리 = Distance.valueOf(10);
		강남_여의도_거리 = Distance.valueOf(100);
		판교_여의도_거리 = 강남_판교_거리.plus(강남_여의도_거리);

		노선목록 = asList(
				new Line("신분당선", "red", 강남역, 판교역, Distance.valueOf(10)),
				new Line("9호선", "gold", 강남역, 여의도역, Distance.valueOf(100)));
	}

	@Test
	void findPath() {
		// given
		when(lineQueryService.findAllLines()).thenReturn(노선목록);
		when(stationService.findById(1L)).thenReturn(판교역);
		when(stationService.findById(2L)).thenReturn(여의도역);

		// when
		ShortestPathResponse 최단_경로_응답 = pathService.findPath(1L, 2L);

		// then
		assertThat(최단_경로_응답.getStations()).containsExactly(판교역, 강남역, 여의도역);
		assertThat(최단_경로_응답.getDistance()).isEqualTo(판교_여의도_거리.getDistance());
	}
}