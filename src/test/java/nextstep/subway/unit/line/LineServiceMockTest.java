package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.SectionService;
import nextstep.subway.section.SectionCreateRequest;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock private LineRepository lineRepository;
    @Mock private SectionRepository sectionRepository;
	@Mock private StationService stationService;

	@InjectMocks private LineService lineService;
	@InjectMocks private SectionService sectionService;

	@DisplayName("지하철 노선에 구간을 등록할 수 있다")
    @Test
    void addSection() {
        // given lineRepository, stationService stub 설정을 통해 초기값 셋팅
		when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("4호선", "#00A5DE")));
		when(sectionRepository.save(any(Section.class))).then(AdditionalAnswers.returnsFirstArg());
		when(stationService.findStationById(1L)).thenReturn(new Station("사당역"));
		when(stationService.findStationById(2L)).thenReturn(new Station("금정역"));

		// when sectionService.addSection 호출
		sectionService.addSection(1L, new SectionCreateRequest(1L, 2L, 10));

        // then lineService.findById 메서드를 통해 검증
		Line line4 = lineService.findLineEntityById(1L);
		assertAll(
			() -> assertThat(line4.getName()).isEqualTo("4호선"),
			() -> assertThat(line4.getColor()).isEqualTo("#00A5DE")
		);
	}
}
