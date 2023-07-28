package nextstep.subway.unit;

import nextstep.subway.applicaion.LineSectionService;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineSectionService lineSectionService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 판교역 = new Station(1L, "판교역");
        Station 정자역 = new Station(2L, "정자역");
        Station 미금역 = new Station(3L, "미금역");

        Line 신분당선 = new Line(1L, "신분당선", "red", new ArrayList<>());
        Section 판교_정자_구간 = new Section(1L, 신분당선, 판교역, 정자역, 10);
        신분당선.getSections().add(판교_정자_구간);

        Section 정자_미금_구간 = new Section(신분당선, 정자역, 미금역, 10);

        when(stationService.findById(2L)).thenReturn(정자역);
        when(stationService.findById(3L)).thenReturn(미금역);

        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        // lineService.addSection 호출
        lineSectionService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getSections()).contains(정자_미금_구간);
    }
}
