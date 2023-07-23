package nextstep.subway.unit;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;
import nextstep.subway.facade.SectionFacade;
import nextstep.subway.fixture.unit.entity.LineFixture;
import nextstep.subway.fixture.unit.entity.StationFixture;
import nextstep.subway.service.LineService;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.request.SectionRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SectionFacadeMockTest {

    @Mock
    private SectionService sectionService;

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @InjectMocks
    private SectionFacade sectionFacade;

    @Test
    void addSection() {
        // given
        int distance = 10;

        Line line = LineFixture.of(1L,"1호선", "green");
        Station 강남역 = StationFixture.of(1);
        Station 양재역 = StationFixture.of(2);
        Station 남영역 = StationFixture.of(3);

        line.addSection(강남역, 양재역, distance);
        SectionRequest sectionRequest = new SectionRequest(남영역.getId(), 양재역.getId(), distance);

        //given
        when(lineService.findById(anyLong())).thenReturn(line);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(stationService.findById(남영역.getId())).thenReturn(남영역);

        // when
        sectionFacade.addSection(line.getId(), sectionRequest);

        // then
        Assertions.assertThat(line.getSectionList()).hasSize(2);

    }
}
