package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    Station 양재역;
    Station 강남역;
    Line 신분당선;
    final Long 신분당선_ID = 4L;
    final Long 양재역_ID = 1L;
    final Long 강남역_ID = 2L;

    @BeforeEach
    void setUp(){
        양재역 = new Station("양재역");
        강남역 = new Station("강남역");
        신분당선 = new Line("신분당선", "yellow");
    }

    @Test
    void addSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 강남역_ID, 10);
        given(stationService.findById(양재역_ID)).willReturn(양재역);
        given(stationService.findById(강남역_ID)).willReturn(강남역);
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, sectionRequest);

        // then
        Line savedLine = lineService.findByLineId(신분당선_ID);
        assertThat(savedLine.getName()).isEqualTo("신분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
    }

    @Test
    void saveLine(){
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 양재역_ID, 강남역_ID, 10);
        given(lineRepository.save(any())).willReturn(신분당선);
        given(stationService.findById(lineRequest.getUpStationId())).willReturn(양재역);
        given(stationService.findById(lineRequest.getDownStationId())).willReturn(강남역);
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.of(신분당선));

        //when
        lineService.saveLine(lineRequest);

        //then
        Line savedLine = lineService.findByLineId(신분당선_ID);
        assertThat(savedLine.getName()).isEqualTo("신분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
    }

    @Test
    void updateLine(){
        //given
        final Long 청계산입구역_ID = 3L;
        LineRequest lineRequest = new LineRequest("분당선", "yellow", 양재역_ID, 청계산입구역_ID, 10);
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.of(신분당선));

        //when
        lineService.updateLine(신분당선_ID, lineRequest);

        //then
        Line savedLine = lineService.findByLineId(신분당선_ID);
        assertThat(savedLine.getName()).isEqualTo("분당선");
        assertThat(savedLine.getColor()).isEqualTo("yellow");
    }

    @Test
    void deleteLine(){
        //given
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.empty());

        //when
        lineService.deleteLine(신분당선_ID);

        //then
        assertThatThrownBy(() -> {
            lineService.findByLineId(신분당선_ID);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteSection(){
        //given
        Section section = new Section(신분당선, 양재역, 강남역, 10);
        신분당선.getSections().add(section);
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.of(신분당선));
        given(stationService.findById(강남역_ID)).willReturn(강남역);

        //when
        lineService.deleteSection(신분당선_ID, 강남역_ID);

        //then
        Line savedLine = lineService.findByLineId(신분당선_ID);
        assertThat(savedLine.getSections().size()).isEqualTo(0);
    }
}
