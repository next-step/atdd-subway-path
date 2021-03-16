package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.of(new Line()));
        when(stationService.findAllStations()).thenReturn(Lists.newArrayList(new StationResponse()));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.findLineById 메서드를 통해 검증 // TODO ?
        assertThat(lineService.findLineById(이호선.getId()).getSections().getUpStation().getName()).isEualTo(강남역.getName());
        assertThat(lineService.findLineById(이호선.getId()).getSections().getDownStation().getName()).isEualTo(역삼역.getName());
    }
}
