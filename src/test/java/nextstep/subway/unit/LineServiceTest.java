package nextstep.subway.unit;

import static nextstep.subway.utils.LineFixture.신분당선_색상;
import static nextstep.subway.utils.LineFixture.신분당선_이름;
import static nextstep.subway.utils.StationFixture.논현역_이름;
import static nextstep.subway.utils.StationFixture.신사역_이름;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.application.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionAddRequest;

@SpringBootTest
@Transactional
public class LineServiceTest {

	@Autowired
	private StationRepository stationRepository;

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private LineService lineService;

	@DisplayName("지하철 노선에 구간을 추가한다.")
	@Test
	void addSection() {
		// given
		Station savedStation = stationRepository.save(new Station(1L, 신사역_이름));
		Station savedStation2 = stationRepository.save(new Station(2L, 논현역_이름));
		Line savedLine = lineRepository.save(new Line(1L, 신분당선_이름, 신분당선_색상));
		SectionAddRequest sectionAddRequest = new SectionAddRequest(savedStation.getId(),
			savedStation2.getId(), 10);

		// when
		LineResponse actual = lineService.addSection(savedLine.getId(), sectionAddRequest);

		// then
		Assertions.assertThat(actual.getStations())
			.usingRecursiveComparison()
			.isEqualTo(List.of(savedStation, savedStation2));
	}
}
