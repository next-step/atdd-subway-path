package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Station 강남구청역 = new Station("강남구청역");
        Station 압구정로데오역 = new Station("압구정로데오역");
        Line 수인분당선 = new Line("수인분당선", "bg-yellow-600");
        int distance = 10;

        when(stationService.findById(강남구청역.getId())).thenReturn(강남구청역);
        when(stationService.findById(압구정로데오역.getId())).thenReturn(압구정로데오역);
        when(lineRepository.findById(수인분당선.getId())).thenReturn(Optional.of(수인분당선));

        // when
        lineService.addSection(수인분당선.getId(),
            new SectionRequest(강남구청역.getId(), 압구정로데오역.getId(), distance));

        // then
        수인분당선 = lineService.findLineById(수인분당선.getId());
        Sections sections = 수인분당선.getSections();
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(강남구청역.getId());
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(압구정로데오역.getId());
        assertThat(sections.get(0).getDistance()).isEqualTo(distance);
    }
}
