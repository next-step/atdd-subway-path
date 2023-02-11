package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

	@Autowired private StationRepository stationRepository;
	@Autowired private LineRepository lineRepository;

	@Autowired private LineService lineService;

	private Line line;
	private Station upStation;
	private Station downStation;

	@BeforeEach
	void setup() {
		upStation = new Station("사당");
		downStation = new Station("금정");
		stationRepository.save(upStation);
		stationRepository.save(downStation);

		line = new Line("4호선", "#00A5DE");
		lineRepository.save(line);
	}

	@DisplayName("지하철 노선에 구간을 등록할 수 있다")
	@Test
	void addSection() {
		// given stationRepository와 lineRepository를 활용하여 초기값 셋팅

		// when lineService.addSection 호출
		lineService.addSection(line.getId(), new SectionRequest(downStation.getId(), upStation.getId(), 10));

		// then
		// line.getSections 메서드를 통해 검증
		List<Section> sections = line.getSections();
		assertThat(sections.get(0).getUpStation()).isEqualTo(upStation);
		assertThat(sections.get(0).getDownStation()).isEqualTo(downStation);
	}
}
