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
import org.junit.jupiter.api.DisplayName;
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
    Section 양재역_강남역_구간;
    final Integer 구간_길이 = 15;

    @BeforeEach
    void setUp() {
        양재역 = new Station(1L, "양재역");
        강남역 = new Station(2L, "강남역");
        신분당선 = new Line(4L, "신분당선", "yellow");
        양재역_강남역_구간 = new Section(5L, 신분당선, 양재역, 강남역, 구간_길이);
        신분당선.getSections().add(양재역_강남역_구간);
    }

    @Test
    void saveLine() {
        //given
        final Line 분당선 = new Line(6L, "분당선", "red");
        LineRequest lineRequest = new LineRequest("분당선", "red", 양재역.getId(), 강남역.getId(), 구간_길이);
        given(lineRepository.save(any())).willReturn(분당선);
        given(stationService.findById(lineRequest.getUpStationId())).willReturn(양재역);
        given(stationService.findById(lineRequest.getDownStationId())).willReturn(강남역);
        given(lineRepository.findById(분당선.getId())).willReturn(Optional.of(분당선));

        //when
        lineService.saveLine(lineRequest);

        //then
        Line savedLine = lineService.findByLineId(분당선.getId());
        assertThat(savedLine.getName()).isEqualTo("분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
    }

    @Test
    void updateLine() {
        //given
        final Long 청계산입구역_ID = 3L;
        LineRequest lineRequest = new LineRequest("분당선", "yellow", 양재역.getId(), 청계산입구역_ID, 10);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        //when
        lineService.updateLine(신분당선.getId(), lineRequest);

        //then
        Line savedLine = lineService.findByLineId(신분당선.getId());
        assertThat(savedLine.getName()).isEqualTo("분당선");
        assertThat(savedLine.getColor()).isEqualTo("yellow");
    }

    @Test
    void deleteLine() {
        //given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.empty());

        //when
        lineService.deleteLine(신분당선.getId());

        //then
        assertThatThrownBy(() -> {
            lineService.findByLineId(신분당선.getId());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteSection() {
        //given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));
        given(stationService.findById(강남역.getId())).willReturn(강남역);

        //when
        lineService.deleteSection(신분당선.getId(), 강남역.getId());

        //then
        Line savedLine = lineService.findByLineId(신분당선.getId());
        assertThat(savedLine.getSections().size()).isEqualTo(0);
    }

    @DisplayName("구간을 등록 시에 역 사이에 새로운 역을 등록")
    @Test
    void addSectionWhenRegisterBetweenSections() {
        // given
        final Integer distance = 10;
        Station 중간역 = new Station(3L, "중간역");
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 중간역.getId(), distance);
        given(stationService.findById(양재역.getId())).willReturn(양재역);
        given(stationService.findById(중간역.getId())).willReturn(중간역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line savedLine = lineService.findByLineId(신분당선.getId());
        assertThat(savedLine.getName()).isEqualTo("신분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(2);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(중간역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
        assertThat(savedLine.getSections().get(0).getDistance()).isEqualTo(구간_길이 - distance);
        assertThat(savedLine.getSections().get(1).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(1).getDownStation()).isEqualTo(중간역);
        assertThat(savedLine.getSections().get(1).getDistance()).isEqualTo(distance);
    }

    @DisplayName("구간을 등록 시에 새로운 역을 상행 종점으로 등록")
    @Test
    void addSectionWhenRegisterUpStation() {
        // given
        Station 상행역 = new Station(3L, "상행역");
        SectionRequest sectionRequest = new SectionRequest(상행역.getId(), 양재역.getId(), 10);
        given(stationService.findById(양재역.getId())).willReturn(양재역);
        given(stationService.findById(상행역.getId())).willReturn(상행역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line savedLine = lineService.findByLineId(신분당선.getId());
        assertThat(savedLine.getName()).isEqualTo("신분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(2);
        assertThat(savedLine.getSections().get(1).getUpStation()).isEqualTo(상행역);
        assertThat(savedLine.getSections().get(1).getDownStation()).isEqualTo(양재역);
    }

    @DisplayName("구간을 등록 시에 새로운 역을 히행 종점으로 등록")
    @Test
    void addSectionWhenRegisterDownStation(){
        // given
        Station 하행역 = new Station(3L, "하행역");
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 하행역.getId(), 10);
        given(stationService.findById(강남역.getId())).willReturn(강남역);
        given(stationService.findById(하행역.getId())).willReturn(하행역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line savedLine = lineService.findByLineId(신분당선.getId());
        assertThat(savedLine.getName()).isEqualTo("신분당선");
        assertThat(savedLine.getSections().size()).isEqualTo(2);
        assertThat(savedLine.getSections().get(1).getUpStation()).isEqualTo(강남역);
        assertThat(savedLine.getSections().get(1).getDownStation()).isEqualTo(하행역);
    }

    @DisplayName("구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void cannotAddSectionIfSectionDistanceIsLargerThanLineDistance(){
        // given
        Station 중간역 = new Station(3L, "중간역");
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 중간역.getId(), 15);
        given(stationService.findById(양재역.getId())).willReturn(양재역);
        given(stationService.findById(중간역.getId())).willReturn(중간역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        assertThatThrownBy(() -> {
            lineService.addSection(신분당선.getId(), sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("지하철 노선에 구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다");
    }

    @DisplayName("구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없다")
    @Test
    void cannotAddSectionIfUpStationAndDownStationAlreadyExisted(){
        // given
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 강남역.getId(), 10);
        given(stationService.findById(양재역.getId())).willReturn(양재역);
        given(stationService.findById(강남역.getId())).willReturn(강남역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        assertThatThrownBy(() -> {
            lineService.addSection(신분당선.getId(), sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("지하철 노선에 구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없습니다");
    }

    @DisplayName("구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void cannotAddSectionIfSectionUpStationAndDownStationNotExistedInLine(){
        // given
        Station 새로운_상행역 = new Station("새로운 상행역");
        Station 새로운_하행역 = new Station("새로운 하행역");

        SectionRequest sectionRequest = new SectionRequest(새로운_상행역.getId(), 새로운_하행역.getId(), 10);
        given(stationService.findById(새로운_상행역.getId())).willReturn(새로운_상행역);
        given(stationService.findById(새로운_하행역.getId())).willReturn(새로운_하행역);
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));

        // when
        assertThatThrownBy(() -> {
            lineService.addSection(신분당선.getId(), sectionRequest);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("지하철 노선에 구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다");
    }
}

