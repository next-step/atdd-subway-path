package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.*;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Station 영등포역;
    private Station 신도림역;
    private Station 구로역;
    private Line 일호선;
    private Line 이호선;

    @BeforeEach
    public void setUp() {
        stub();
    }

    @Test
    void addSection() {
        // given
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);

        // when
        lineService.addSection(일호선.getId(), new SectionRequest(영등포역.getId(), 신도림역.getId(), 20));

        // then
        Line line = lineService.findLineById(일호선.getId());
        assertAll(() -> assertThat(line.getSections().size()).isEqualTo(1), () -> assertThat(line.getStations()).contains(영등포역, 신도림역));
    }

    @Test
    void showLines() {
        // given
        when(lineRepository.findAll()).thenReturn(Arrays.asList(일호선, 이호선));

        // when
        List<LineResponse> allLines = lineService.showLines();

        // then
        assertAll(
                () -> assertThat(allLines)
                        .hasSize(2),
                () -> assertThat(allLines)
                        .extracting("name").containsExactly(일호선.getName(), 이호선.getName()),
                () -> assertThat(allLines)
                        .extracting("color").containsExactly(일호선.getColor(), 이호선.getColor())
        );
    }

    @Test
    void updateLine() {
        // given
        String newLineName = "1호선";
        String newLineColor = "blue";
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        LineRequest lineRequest = new LineRequest();
        lineRequest.setName(newLineName);
        lineRequest.setColor(newLineColor);


        // when
        lineService.updateLine(이호선.getId(), lineRequest);

        // then
        LineResponse newResponse = lineService.findById(이호선.getId());
        assertAll(
                () -> assertThat(newResponse.getColor()).isEqualTo(newLineColor),
                () -> assertThat(newResponse.getName()).isEqualTo(newLineName)
        );
    }

    @Test
    void deleteSection() {
        // given
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        lineService.addSection(일호선.getId(), new SectionRequest(영등포역.getId(), 신도림역.getId(), 20));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 구로역.getId(), 20));

        // when
        lineService.deleteSection(일호선.getId(), 구로역.getId());

        // then
        Line line = lineService.findLineById(일호선.getId());
        assertAll(
                () -> assertThat(line.getStations()).hasSize(2),
                () -> assertThat(line.getStations()).containsExactly(영등포역, 신도림역)
        );
    }

    private void stub() {

        영등포역 = new Station("영등포역");
        신도림역 = new Station("신도림역");
        구로역 = new Station("구로역");
        ReflectionTestUtils.setField(영등포역, "id", 1L);
        ReflectionTestUtils.setField(신도림역, "id", 2L);
        ReflectionTestUtils.setField(구로역, "id", 3L);

        일호선 = new Line("1호선", "blue");
        이호선 = new Line("2호선", "green");
        ReflectionTestUtils.setField(일호선, "id", 1L);
        ReflectionTestUtils.setField(이호선, "id", 2L);
    }
}
