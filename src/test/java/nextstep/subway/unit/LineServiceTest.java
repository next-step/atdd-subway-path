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

	private Line 이호선;
	private Station 강남역;
	private Station 선릉역;
	private Station 역삼역;
	private SectionRequest 강남역_선릉역_구간_생성_요청_데이터;

	@BeforeEach
	void setUp() {
		강남역 = new Station("강남역");
		선릉역 = new Station("선릉역");
		역삼역 = new Station("역삼역");
		stationRepository.save(강남역);
		stationRepository.save(선릉역);
		stationRepository.save(역삼역);

		이호선 = new Line("2호선", "green");
		lineRepository.save(이호선);

		강남역_선릉역_구간_생성_요청_데이터 = new SectionRequest(강남역.getId(), 선릉역.getId(), 10);
	}

	@DisplayName("구간 추가")
	@Test
	void addSection() {
		// when
		lineService.addSection(이호선.getId(), 강남역_선릉역_구간_생성_요청_데이터);

		// then
		assertThat(이호선.getSections().getSections()).isNotEmpty();
	}
}
