package nextstep.subway.unit;

import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.service.PathService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
public class PathServiceTest {
	private PathService pathService;
	@Mock
	private StationService stationService;

	private static final Long 첫번째_역_id = 1L;
	private static final Long 두번째_역_id = 2L;
	private static final Long 세번째_역_id = 3L;
	private static final int 첫번째_역_두번째_역_길이 = 10;
	private static final int 두번째_역_세번째_역_길이 = 6;
	private static final int 첫번째_역_세번째_역_길이 = 12;

	@BeforeEach
	void setup() {
		pathService = new PathService(stationService);
		Line 일호선 = new Line("일노선", "파란색", 첫번째_역_id, 두번째_역_id, 첫번째_역_두번째_역_길이);
		일호선.addSection(new Section(일호선, 두번째_역_id, 세번째_역_id, 두번째_역_세번째_역_길이));

		new Line("사호선", "하늘색", 첫번째_역_id, 세번째_역_id, 첫번째_역_세번째_역_길이);

		given(stationService.findStationById(첫번째_역_id))
				.willReturn(new StationResponse(첫번째_역_id, "첫번째_역"));
		given(stationService.findStationById(세번째_역_id))
				.willReturn(new StationResponse(세번째_역_id, "세번째_역"));
	}

	@Test
	void getPath() {
		PathResponse response = pathService.getPath(첫번째_역_id, 세번째_역_id);

		assertThat(response.getStations()).hasSize(2);
		assertThat(response.getDistance()).isEqualTo(첫번째_역_세번째_역_길이);
	}
}
