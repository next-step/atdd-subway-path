package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NotFoundLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.unit.LineUnitTestHelper.강남_TO_역삼_구간_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.교대_TO_역삼_구간_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.신분당선_요청_객체_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.이호선_요청_객체_만들기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 정자역;
    private Station 판교역;
    private Line 이호선;
    private LineRequest 이호선_요청_객체;
    private LineRequest 신분당선_요청_객체;
    private SectionRequest 교대_TO_역삼;
    private SectionRequest 강남_TO_역삼;

    @BeforeEach
    void setFixtures() {
        교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 1L);

        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);

        정자역 = new Station("정자역");
        ReflectionTestUtils.setField(정자역, "id", 3L);

        판교역 = new Station("판교역");
        ReflectionTestUtils.setField(판교역, "id", 4L);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 5L);

        이호선 = new Line("2호선", "bg-green-600");
        ReflectionTestUtils.setField(이호선, "id", 1L);

        교대_TO_역삼 = 교대_TO_역삼_구간_만들기(교대역, 역삼역);
        강남_TO_역삼 = 강남_TO_역삼_구간_만들기(강남역, 역삼역);

        이호선_요청_객체 = 이호선_요청_객체_만들기(교대역, 역삼역);
        신분당선_요청_객체 = 신분당선_요청_객체_만들기(정자역, 판교역);
    }

    @Test
    void saveLine() {
        // given
        givenToSaveLine();

        // when
        lineService.saveLine(이호선_요청_객체);

        // then
        Line findLine = lineRepository.findById(이호선.getId()).get();
        assertThat(findLine.getId()).isEqualTo(1L);
    }

    @Test
    void addSection() {
        // given
        when(stationService.findStationById(1L)).thenReturn(교대역);
        when(stationService.findStationById(2L)).thenReturn(역삼역);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(교대_TO_역삼, 이호선.getId());

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        Sections sections = line.getSections();
        assertThat(sections.getSections()).hasSize(1);
        assertThat(sections.getDownStationEndPoint()).isEqualTo(역삼역);
    }

    @Test
    void findLineById() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        // when
        LineResponse lineResponse = lineService.findLineById(이호선.getId());

        // then
        assertThat(lineResponse.getName()).isEqualTo(이호선.getName());
    }

    @Test
    void findAllLines() {
        // given
        List<Line> lines = Collections.singletonList(이호선);
        when(lineRepository.findAll()).thenReturn(lines);

        // when
        List<LineResponse> lineResponses = lineService.findAllLines();

        // then
        assertThat(lineResponses).hasSize(lines.size());
    }

    @Test
    void updateLine() {
        // given
        givenToSaveLine();
        lineService.saveLine(이호선_요청_객체);

        // when
        lineService.updateLine(이호선.getId(), 신분당선_요청_객체);

        // then
        LineResponse lineResponse = lineService.findLineById(이호선.getId());
        assertThat(lineResponse.getName()).isEqualTo(신분당선_요청_객체.getName());
    }

    @Test
    void deleteLine() {
        // given
        givenToSaveLine();
        lineService.saveLine(이호선_요청_객체);

        // when
        lineService.deleteLineById(이호선.getId());
        when(lineRepository.findById(1L)).thenThrow(NotFoundLineException.class);

        // then
        assertThatThrownBy(() -> {
            lineService.findLineById(이호선.getId());
        }).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void deleteSection() {
        // given
        when(stationService.findStationById(1L)).thenReturn(교대역);
        when(stationService.findStationById(2L)).thenReturn(역삼역);
        when(stationService.findStationById(5L)).thenReturn(강남역);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));
        lineService.addSection(교대_TO_역삼, 이호선.getId());
        lineService.addSection(강남_TO_역삼, 이호선.getId());

        // when
        lineService.deleteSection(이호선.getId(), 강남역.getId());

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        Sections sections = line.getSections();
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(7);
    }

    private void givenToSaveLine() {
        when(stationService.findStationById(1L)).thenReturn(교대역);
        when(stationService.findStationById(2L)).thenReturn(역삼역);

        Line line = Line.of(이호선_요청_객체.toEntity(), 교대역, 역삼역, 5);
        ReflectionTestUtils.setField(line, "id", 1L);
        when(lineRepository.save(any(line.getClass()))).then(AdditionalAnswers.returnsFirstArg());
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
    }
}
