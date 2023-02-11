package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock private LineRepository lineRepository;
    @Mock private StationService stationService;

    @InjectMocks private LineService lineService;

	private Line line;
	private Station upStation;
	private Station downStation;

	@BeforeEach
	void setup() {
		line = new Line("4호선", "#00A5DE");
		upStation = new Station("사당");
		downStation = new Station("금정");
	}

	@DisplayName("지하철 노선에 구간을 등록할 수 있다")
    @Test
    void addSection() {
        // given lineRepository, stationService stub 설정을 통해 초기값 셋팅
		when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
		when(stationService.findById(upStation.getId())).thenReturn(upStation);
		when(stationService.findById(downStation.getId())).thenReturn(downStation);

		// when lineService.addSection 호출
		SectionRequest sectionCreateRequest = new SectionRequest(downStation.getId(), upStation.getId(), 10);
		lineService.addSection(line.getId(), sectionCreateRequest);

        // then lineService.findLineById 메서드를 통해 검증
		LineResponse lineResponse = lineService.findById(line.getId());
		assertAll(
			() -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
			() -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor()),
			() -> assertThat(lineResponse.getStations().size()).isEqualTo(2)
		);
	}
}
