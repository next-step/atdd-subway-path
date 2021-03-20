package nextstep.subway.line.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.*;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
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

        강남역 = initStation(강남역,"강남역", 1L);
        역삼역 = initStation(역삼역,"역삼역", 2L);
        삼성역 = initStation(삼성역,"삼성역", 3L);

        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @Test
    void saveLine() {
        // given
        when(stationService.findStationById(강남역.getId())).thenReturn(강남역);
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(lineRepository.save(any())).thenReturn(이호선);

        // when
        LineResponse 저장된_이호선 = lineService.saveLine(
                new LineRequest(이호선.getName(), 이호선.getColor(), 강남역.getId(), 역삼역.getId(), 10)
        );

        // then
        assertThat(저장된_이호선).isNotNull();
        assertThat(저장된_이호선.getName()).isEqualTo(이호선.getName());
    }

    @Test
    void findLines() {
        // given
        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(new Line()));

        // when
        List<Line> lines = lineService.findLines();

        // then
        assertThat(lines).hasSize(1);
    }

    @Test
    void findLineResponses() {
        // given
        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(new Line()));

        // when
        List<LineResponse> responses = lineService.findLineResponses();

        // then
        assertThat(responses).hasSize(1);
    }

    @Test
    void findLineById() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.ofNullable(이호선));

        // when
        Line 조회된_이호선 = lineService.findLineById(이호선.getId());

        // then
        assertThat(조회된_이호선).isNotNull();
        assertThat(조회된_이호선.getName()).isEqualTo("2호선");
    }

    @Test
    void findLineResponseById() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.ofNullable(이호선));

        // when
        LineResponse 이호선_리스폰스 = lineService.findLineResponseById(이호선.getId());

        // then
        assertThat(이호선_리스폰스).isNotNull();
        assertThat(이호선_리스폰스.getName()).isEqualTo("2호선");
    }

    @Test
    void updateLine() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.ofNullable(이호선));

        // when
        lineService.updateLine(이호선.getId(),  new LineRequest("3호선", "주황색", 강남역.getId(), 역삼역.getId(), 10));

        // then
        assertThat(이호선.getName()).isEqualTo("3호선");
        assertThat(이호선.getColor()).isEqualTo("주황색");
    }

    @Test
    void deleteLineById() {
        // when / then
        assertThatCode(() ->
                lineService.deleteLineById(이호선.getId())
        ).doesNotThrowAnyException();
        verify(lineRepository).deleteById(anyLong());
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line 구간생성된_이호선 = lineService.findLineById(이호선.getId());
        assertThat(구간생성된_이호선).isNotNull();
        assertThat(구간생성된_이호선.getSections().getSections()).hasSize(2);
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(역삼역, 삼성역, 20);
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findStationById(any())).thenReturn(강남역);

        // when
        lineService.removeSection(이호선.getId(), 강남역.getId());

        // then
        assertThat(이호선.getSections().getStations().contains(강남역)).isFalse();
    }

    @Test
    void findShortestPath() {
        // given
        when(lineRepository.findAll()).thenReturn(Arrays.asList(이호선));
        when(stationService.findStationById(강남역.getId())).thenReturn(강남역);
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);

        // when
        PathResponse shortestPath = lineService.findShortestPath(new PathRequest(강남역.getId(), 역삼역.getId()));

        // then
        assertThat(shortestPath).isNotNull();
        assertThat(shortestPath.getDistance()).isEqualTo(10);
    }

    private Station initStation(Station station, String stationName, Long id) {
        station = new Station(stationName);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
