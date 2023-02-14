package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.SectionService;
import nextstep.subway.station.StationRepository;
import nextstep.subway.section.SectionCreateRequest;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;

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
    @Mock private StationRepository stationRepository;

    @InjectMocks private SectionService sectionService;
    @InjectMocks private LineService lineService;

	@DisplayName("지하철 노선에 구간을 등록할 수 있다")
    @Test
    void addSection() {
        // given lineRepository, stationService stub 설정을 통해 초기값 셋팅
		when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("4호선", "#00A5DE")));
		when(sectionRepository.save(any(Section.class))).then(AdditionalAnswers.returnsFirstArg());
		when(stationRepository.findById(1L)).thenReturn(Optional.of(new Station("사당역")));
		when(stationRepository.findById(2L)).thenReturn(Optional.of(new Station("금정역")));

		// when sectionService.addSection 호출
		sectionService.addSection(1L, new SectionCreateRequest(1L, 2L, 10));

        // then lineService.findById 메서드를 통해 검증
		Line line4 = lineService.findById(1L);
		assertAll(
			() -> assertThat(line4.getName()).isEqualTo("4호선"),
			() -> assertThat(line4.getColor()).isEqualTo("#00A5DE"),
			() -> assertThat(line4.getAllStation().size()).isEqualTo(2)
		);
	}
}
