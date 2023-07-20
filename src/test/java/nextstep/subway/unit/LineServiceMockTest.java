package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = new Station(1L, "강남역");
        Station 양재역 = new Station(2L, "양재역");

        when(stationRepository.findById(anyLong()))
                .thenReturn(Optional.of(강남역))
                .thenReturn(Optional.of(양재역));

        Line 신분당선 = new Line("신분당선", "bg-red-600", new Section(양재역, new Station(4L, "역2"), 10));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationRepository);
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);
        lineService.registerSection(1L, sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(2);
    }
}
