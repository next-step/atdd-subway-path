package nextstep.subway.line;

import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.line.section.SectionsUpdateRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    private Station 강남역;
    private Station 선릉역;
    private Station 교대역;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);
        강남역 = new Station(1L, "강남역");
        선릉역 = new Station(2L, "선릉역");
        교대역 = new Station(3L, "교대역");
    }

    @Test
    void addSection() {
        // given
        Line line = new Line(
                1L,
                "신분당선",
                "bg-red-600",
                Sections.from(new ArrayList<>(List.of(new Section(
                        강남역,
                        선릉역,
                        10L)))),
                10L);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationRepository.findById(선릉역.getId())).thenReturn(Optional.of(선릉역));
        when(stationRepository.findById(교대역.getId())).thenReturn(Optional.of(교대역));

        // when
        SectionsUpdateRequest 선릉역_부터_교대역 = new SectionsUpdateRequest(3L, 2L, 10L);
        LineResponse actual = lineService.addSection(1L, 선릉역_부터_교대역);

        // then
        LineResponse expected = lineService.findLine(actual.getId());
        assertThat(actual).isEqualTo(expected);
    }
}
