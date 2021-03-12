package nextstep.subway.line.domain;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
public class LineTest {
    @MockBean
    private LineRepository lineRepository;

    @MockBean
    private StationService stationService;

    private LineService lineService;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setup() {
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
    void getStations() {
        //given
        when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));

        //when
        LineResponse response = lineService.findLineResponseById(이호선.getId());

        //then
        assertThat(response.getStations()).hasSize(2);
    }

    @Test
    void addSection() {
        //given
        when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);

        //when
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 10);
        lineService.addSection(이호선.getId(), sectionRequest);
        LineResponse response = lineService.findLineResponseById(이호선.getId());

        //then
        assertThat(response.getStations()).hasSize(3);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {

    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        //given
        when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
        when(stationService.findStationById(강남역.getId())).thenReturn(강남역);
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);

        //when, then
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);
        assertThatThrownBy(()-> lineService.addSection(이호선.getId(), sectionRequest))
                .isInstanceOf(RuntimeException.class);

    }

    @Test
    void removeSection() {
        //given
        when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 10);
        lineService.addSection(이호선.getId(), sectionRequest);

        //when
        lineService.removeSection(이호선.getId(), 삼성역.getId());
        LineResponse response = lineService.findLineResponseById(이호선.getId());

        //then
        assertThat(response.getStations()).hasSize(2);

    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        //when,then
        assertThatThrownBy(()-> lineService.removeSection(이호선.getId(), 역삼역.getId()))
                .isInstanceOf(RuntimeException.class);

    }
}
