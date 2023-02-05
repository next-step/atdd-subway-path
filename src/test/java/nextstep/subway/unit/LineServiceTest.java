package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.SectionFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
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

	@BeforeEach
	void setUp() {
		stationRepository.save(동대문);
		stationRepository.save(동대문역사문화공원);
		stationRepository.save(충무로);

		lineRepository.save(LINE_4);
	}

	@Test
	void addSection() {
		// given
		SectionRequest 구간_추가_요청 = 구간_추가_요청(동대문_ID, 동대문역사문화공원_ID, 10);

		// when
		lineService.addSection(LINE_4_ID, 구간_추가_요청);

		// then
		Line line = lineRepository.findById(LINE_4_ID).get();
		assertThat(line.getSections()).hasSize(1);
	}

	@Test
	void getStations() {
		// given
		SectionRequest 동대문_동대문역사문화공원 = 구간_추가_요청(동대문_ID, 동대문역사문화공원_ID, 10);
		SectionRequest 동대문역사문화공원_충무로 = 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5);

		// when
		lineService.addSection(LINE_4_ID, 동대문_동대문역사문화공원);
		lineService.addSection(LINE_4_ID, 동대문역사문화공원_충무로);

		// then
		LineResponse lineResponse = lineService.findById(LINE_4_ID);
		List<StationResponse> stationResponses = lineResponse.getStations();

		assertAll(
			() -> assertThat(stationResponses).hasSize(3),
			() -> assertThat(stationResponses).extracting("id")
				.containsExactly(
					동대문_ID,
					동대문역사문화공원_ID,
					충무로_ID
				)
		);
	}
}
