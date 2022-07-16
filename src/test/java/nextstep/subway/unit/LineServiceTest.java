package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@SpringBootTest
@Transactional
public class LineServiceTest {
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private LineService lineService;

	@Test
	void addSection() {
		// given
		// stationRepository와 lineRepository를 활용하여 초기값 셋팅
		Station 강남역 = stationRepository.save(new Station("강남역"));
		Station 삼성역 = stationRepository.save(new Station("삼성역"));
		Line 이호선 = lineRepository.save(new Line("2호선", "Green"));

		// when
		// lineService.addSection 호출
		lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 2));

		// then
		// line.getSections 메서드를 통해 검증
		assertThat(이호선.getSections()).hasSize(1);

	}
}
