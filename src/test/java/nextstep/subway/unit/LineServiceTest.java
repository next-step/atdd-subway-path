package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import nextstep.subway.exception.SubwayException;

@SpringBootTest
@Transactional
public class LineServiceTest {
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private LineService lineService;

	Station 강남역;
	Station 양재역;
	Station 정자역;
	Line 신분당선;

	@BeforeEach
	void setUp() {
		// given
		// stationRepository와 lineRepository를 활용하여 초기값 셋팅
		강남역 = stationRepository.save(new Station("강남역"));
		양재역 = stationRepository.save(new Station("양재역"));
		정자역 = stationRepository.save(new Station("정자역"));

		Line line = new Line("신분당선", "bg-red-600");
		line.addSection(new Section(line, 강남역, 양재역, 10));

		신분당선 = lineRepository.save(line);
	}

	@DisplayName("섹션 추가 기능")
	@Test
	void addSection() {
		// when
		// lineService.addSection 호출
		lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 정자역.getId(), 10));

		// then
		// line.getSections 메서드를 통해 검증
		Line line = lineService.findById(신분당선.getId());
		assertThat(line.lastIndexOfSections()).isEqualTo(1);
	}

	@DisplayName("구간이 하나일때 역을 제거하려고 하면 에러")
	@Test
	void deleteSectionWithException() {
		// when
		Line line = lineService.findById(신분당선.getId());
		assertThatThrownBy(() -> {line.deleteSection(강남역);}).isInstanceOf(
			SubwayException.CanNotDeleteException.class);
	}
}
