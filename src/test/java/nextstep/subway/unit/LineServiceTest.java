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
import nextstep.subway.domain.Section;
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
		Station 양재역 = stationRepository.save(new Station("양재역"));
		Station 정자역 = stationRepository.save(new Station("정자역"));

		Line line = new Line("신분당선", "bg-red-600");
		line.addSection(new Section(line, 강남역, 양재역, 10));
		Line 신분당선 = lineRepository.save(line);
		// when
		// lineService.addSection 호출
		lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 정자역.getId(), 10));

		// then
		// line.getSections 메서드를 통해 검증
		Line line1 = lineService.findById(신분당선.getId());
		assertThat(line1.sectionsSize()).isEqualTo(2);
	}
}
