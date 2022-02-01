package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    private LineService lineService;
    private Station 교대역;
    private Station 역삼역;
    private Line 이호선;
    private SectionRequest sectionRequest = new SectionRequest();

    @BeforeEach
    void setFixtures() {
        // given
        lineService = new LineService(lineRepository, stationRepository);

        교대역 = new Station("교대역");
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(교대역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);

        이호선 = new Line("2호선", "bg-red-600");
        ReflectionTestUtils.setField(이호선, "id", 1L);

        ReflectionTestUtils.setField(sectionRequest, "downStationId", 역삼역.getId());
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 교대역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 5);
    }

    @Test
    void addSection() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(역삼역));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(sectionRequest, 이호선.getId());

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(이호선.getId()).get();
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(1);
        assertThat(sections.getDownStationEndPoint()).isEqualTo(역삼역);
    }
}
