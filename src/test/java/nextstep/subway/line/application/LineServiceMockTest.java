package nextstep.subway.line.application;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Station 을지로3가역;
    private Station 을지로입구역;
    private Station 시청역;
    private Station 충정로역;

    private Line 이호선;

    private final static int 기본_구간_길이 = 50;
    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);

        역_생성();
        이호선_생성();
    }

    void 역_생성() {
        을지로3가역 = new Station("을지로3가역");
        ReflectionTestUtils.setField(을지로3가역, "id", 1L);

        을지로입구역 = new Station("을지로입구역");
        ReflectionTestUtils.setField(을지로입구역, "id", 2L);

        시청역 = new Station("시청역");
        ReflectionTestUtils.setField(시청역, "id", 3L);

        충정로역 = new Station("충정로역");
        ReflectionTestUtils.setField(충정로역, "id", 4L);
    }

    void 이호선_생성() {
        이호선 = new Line("2호선", "green", 을지로3가역, 시청역, 기본_구간_길이);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @DisplayName("지하철 노선을 생성")
    @Test
    void addLine() {
        //given
        given(stationService.findStationById(을지로3가역.getId())).willReturn(을지로3가역);
        given(stationService.findStationById(을지로입구역.getId())).willReturn(을지로입구역);
        given(lineRepository.save(any(Line.class))).willReturn(이호선);

        //when
        LineRequest request = new LineRequest("이호선", "Green", 을지로3가역.getId(), 을지로입구역.getId(), 10);
        LineResponse lineResponse = lineService.saveLine(request);

        //then
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @DisplayName("지하철 구간을 생성")
    @Test
    void addSection() {
        // given
        given(stationService.findStationById(을지로3가역.getId())).willReturn(을지로3가역);
        given(stationService.findStationById(을지로입구역.getId())).willReturn(을지로입구역);
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));

        // when
        SectionRequest request = new SectionRequest(을지로3가역.getId(), 을지로입구역.getId(), 3);
        lineService.addSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSortedStations()).hasSize(3);
    }

    @DisplayName("라인에 등록되지 않은 상/하행역으로 구간을 생성")
    @Test
    void addSectionWithNotRegistedOnLine() {
        // given
        given(stationService.findStationById(충정로역.getId())).willReturn(충정로역);
        given(stationService.findStationById(을지로입구역.getId())).willReturn(을지로입구역);
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));

        // when
        SectionRequest request = new SectionRequest(충정로역.getId(), 을지로입구역.getId(), 3);

        assertThatThrownBy(() -> lineService.addSection(이호선.getId(), request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("상/하행역 중 하나는 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("라인에 이미 다 등록된 상/하행역으로 구간을 생성")
    @Test
    void addSectionWithRegistedOnLine() {
        // given
        given(stationService.findStationById(을지로3가역.getId())).willReturn(을지로3가역);
        given(stationService.findStationById(시청역.getId())).willReturn(시청역);
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));

        // when
        SectionRequest request = new SectionRequest(을지로3가역.getId(), 시청역.getId(), 20);

        assertThatThrownBy(() -> lineService.addSection(이호선.getId(), request))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("상/하행역 모두 이미 등록되어있는 역입니다.");
    }

    @DisplayName("지하철 최소구간 갯수일 때 삭제 시 오류")
    @Test
    void removeSectionWithMinimumSection() {
        // given
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));
        given(stationService.findStationById(시청역.getId())).willReturn(시청역);

        // when
        assertThatThrownBy(() -> lineService.removeSection(이호선.getId(), 시청역.getId()))
                        .isInstanceOf(ApplicationException.class)
                        .hasMessage("구간은 최소하나는 등록되어있어야 합니다.");
    }

    @DisplayName("지하철 삭제 시 하행종점이 아닐 때 정상 삭제")
    @Test
    void removeSectionWithUpstation() {
        // given
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));
        given(stationService.findStationById(시청역.getId())).willReturn(시청역);
        이호선.addSection(시청역, 충정로역,9);

        // when
        lineService.removeSection(이호선.getId(), 시청역.getId());

        assertThat(이호선.getLineDistance()).isEqualTo(기본_구간_길이+9);
        assertThat(이호선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("지하철 삭제")
    @Test
    void removeSection() {
        // given
        given(lineRepository.findById(any())).willReturn(Optional.of(이호선));
        given(stationService.findStationById(시청역.getId())).willReturn(시청역);
        이호선.addSection(을지로입구역, 을지로3가역,3);

        // when
        lineService.removeSection(이호선.getId(), 시청역.getId());

        assertThat(lineService.findLineById(이호선.getId()).getSortedStations())
                .extracting(Station::getName)
                .containsExactly(을지로입구역.getName(), 을지로3가역.getName());
    }
}
