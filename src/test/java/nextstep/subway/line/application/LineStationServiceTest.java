package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("지하철 노선 서비스 테스트")
public class LineStationServiceTest {
	private LineRepository lineRepository;
	private StationRepository stationRepository;

	private LineStationService lineStationService;

	@BeforeEach
	void setUp() {
		lineRepository = mock(LineRepository.class);
		stationRepository = mock(StationRepository.class);
		lineStationService = new LineStationService(lineRepository, stationRepository);
	}

	@DisplayName("존재하지 않는 역을 등록한다.")
	@Test
	void 존재하지_않는_역을_등록한다() {
		Long stationId = 1L;
		when(stationRepository.findAllById(Lists.newArrayList(null, stationId))).thenReturn(Lists.newArrayList());
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(stationId, null, 2, 2);
        assertThatThrownBy(
            () -> lineStationService.addLineStation(1L, lineStationCreateRequest)
        ).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("존재하지 않는 역을 이전 역으로 등록한다.")
	@Test
	void 존재하지_않는_역을_이전_역으로_등록한다() {
		Long preStationId = 1L;
		Long stationId = 2L;
		Station station = new Station("회기역");
		when(stationRepository.findAllById(Lists.newArrayList(preStationId, stationId))).thenReturn(Lists.newArrayList(station));
		LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(stationId, preStationId, 2, 2);
		assertThatThrownBy(
			() -> lineStationService.addLineStation(1L, lineStationCreateRequest)
		).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("지하철 노선에 역을 제외한다.")
	@Test
	void removeLineStation() {
	}
}
