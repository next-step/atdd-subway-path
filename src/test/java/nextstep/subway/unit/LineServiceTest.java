package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.SectionFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.IntegrationUnitTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.exception.LineErrorCode;
import nextstep.subway.domain.exception.SectionErrorCode;
import nextstep.subway.domain.exception.SectionRemoveException;
import nextstep.subway.domain.exception.StationErrorCode;
import nextstep.subway.domain.exception.StationNotFoundException;
import nextstep.subway.domain.exception.SubwayBadRequestException;

public class LineServiceTest extends IntegrationUnitTest {
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private LineService lineService;

	@BeforeEach
	void setUp() throws Exception {
		stationRepository.save(동대문);
		stationRepository.save(동대문역사문화공원);
		stationRepository.save(충무로);

		lineRepository.save(LINE_4());
	}

	@Test
	void addSection() {
		// given
		SectionRequest 동대문역사문화공원_충무로 = 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5);

		// when
		lineService.addSection(LINE_4_ID, 동대문역사문화공원_충무로);

		// then
		Line line = lineRepository.findById(LINE_4_ID).get();
		assertThat(line.getSections()).hasSize(2);
	}

	@DisplayName("상행 하행 종점역중 등록되지 않은 역을 요청할 경우 예외가 발생한다")
	@Test
	void 상행_하행_종점역중_등록되지_않은_역을_요청할_경우_예외가_발생한다() {
		SectionRequest 동대문역사문화공원_등록되지않은역 = 구간_추가_요청(동대문역사문화공원_ID, 등록되지않은_역_ID, 5);

		assertThatThrownBy(() -> lineService.addSection(LINE_4_ID, 동대문역사문화공원_등록되지않은역))
			.isInstanceOf(StationNotFoundException.class)
			.hasMessage(StationErrorCode.NOT_FOUND_STATION.getMessage());
	}

	@DisplayName("노선생성시 구간거리를 0으로 요청할 경우 예외가 발생한다")
	@Test
	void 노선생성시_구간거리를_잘못요청할_경우_예외가_발생한다() {
		LineRequest lineRequest = new LineRequest("4호선", "blue", 동대문역사문화공원_ID, 충무로_ID, 0);

		assertThatThrownBy(() -> lineService.saveLine(lineRequest))
			.isInstanceOf(SubwayBadRequestException.class)
			.hasMessage(LineErrorCode.INVALID_SECTION_DISTANCE.getMessage());
	}

	@Test
	void getStations() {
		// given
		SectionRequest 동대문역사문화공원_충무로 = 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5);

		// when
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

	@DisplayName("구간이 한개 이상일때 마지막 구간 제거에 성공한다")
	@Test
	void 구간이_한개_이상일때_마지막_구간_제거에_성공한다() {
		// given
		SectionRequest 동대문역사문화공원_충무로 = 구간_추가_요청(동대문역사문화공원_ID, 충무로_ID, 5);
		lineService.addSection(LINE_4_ID, 동대문역사문화공원_충무로);

		// when
		lineService.deleteSection(LINE_4_ID, 충무로_ID);

		// then
		LineResponse lineResponse = lineService.findById(LINE_4_ID);

		assertThat(lineResponse.getStations()).hasSize(2);
	}

	@DisplayName("구간제거시 상행종점역과 하행종점역만 있을경우 예외가 발생한다")
	@Test
	void 구간제거시_상행종점역과_하행종점역만_있을경우_예외가_발생한다() {
		assertThatThrownBy(() -> lineService.deleteSection(LINE_4_ID, 충무로_ID))
			.isInstanceOf(SectionRemoveException.class)
			.hasMessage(SectionErrorCode.SINGLE_SECTION.getMessage());
	}
}
