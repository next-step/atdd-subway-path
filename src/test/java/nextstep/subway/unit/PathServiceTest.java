package nextstep.subway.unit;

import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 탐색 서비스 단위 테스트")
@SpringBootTest
@Transactional
class PathServiceTest {

	@Autowired
	LineQueryService lineQueryService;
	@Autowired
	LineRepository lineRepository;

	@Autowired
	StationService stationService;
	@Autowired
	StationRepository stationRepository;

	@Autowired
	PathService pathService;

	Station 강남역;
	Station 판교역;
	Station 여의도역;
	Station 광화문역;
	List<Line> 노선목록;

	Distance 강남_판교_거리;
	Distance 강남_여의도_거리;
	Distance 판교_광화문_거리;

	/**
	 * Given
	 * 여의도역--*5호선*--광화문역
	 * |
	 * *9호선*
	 * |
	 * 강남역--*신분당선*--판교역
	 */
	@BeforeEach
	void setUp() {
		강남역 = new Station("강남역");
		판교역 = new Station("판교역");
		여의도역 = new Station("여의도역");
		광화문역 = new Station("광화문역");

		강남_판교_거리 = Distance.valueOf(10);
		강남_여의도_거리 = Distance.valueOf(100);
		판교_광화문_거리 = Distance.valueOf(10 + 100 + 200);

		노선목록 = asList(
				new Line("신분당선", "red", 강남역, 판교역, Distance.valueOf(10)),
				new Line("9호선", "gold", 강남역, 여의도역, Distance.valueOf(100)),
				new Line("5호선", "purple", 여의도역, 광화문역, Distance.valueOf(200))
		);
	}

	@Test
	void findPath() {
		// given
		stationRepository.save(강남역);
		long 판교역_번호 = stationRepository.save(판교역).getId();
		stationRepository.save(여의도역);
		long 광화문역_번호 = stationRepository.save(광화문역).getId();

		lineRepository.saveAll(노선목록);

		// when
		ShortestPathResponse 최단_경로_응답 = pathService.findPath(판교역_번호, 광화문역_번호);

		// then
		assertThat(최단_경로_응답.getStations()).containsExactly(판교역, 강남역, 여의도역, 광화문역);
		assertThat(최단_경로_응답.getDistance()).isEqualTo(판교_광화문_거리.getDistance());
	}
}