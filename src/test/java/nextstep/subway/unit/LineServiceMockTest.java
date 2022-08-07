package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.구로역;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.구로역_신도림역_거리;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.신도림역;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.신도림역_영등포역_거리;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.영등포역;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.일호선;
import static nextstep.subway.unit.LineServiceMockTest.SubwayInfo.이호선;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    private void setup() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);

        Line 일호선 = new Line(1L, "1호선", "blue");
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(), new SectionRequest(구로역.getId(), 신도림역.getId(), 구로역_신도림역_거리));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 영등포역.getId(), 신도림역_영등포역_거리));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(일호선.getId());

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getSections().count()).isEqualTo(2);

    }

    @DisplayName("지하철 노선에 등록된 역 목록 조회")
    @Test
    void getStations() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 일호선 = new Line(1L, "1호선", "blue");

        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);

        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(), new SectionRequest(구로역.getId(), 신도림역.getId(), 구로역_신도림역_거리));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 영등포역.getId(), 신도림역_영등포역_거리));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(일호선.getId());

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getStations().stream().map(Station::getName)).contains("구로역", "신도림역", "영등포역");
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void saveLine() {
        // given
        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(lineRepository.save(any())).thenReturn(일호선);

        // when
        LineResponse lineResponse = lineService.saveLine(new LineRequest("1호선", "blue", 구로역.getId(), 신도림역.getId(), 구로역_신도림역_거리));

        // then
        assertThat(lineResponse.getName()).isEqualTo("1호선");
        assertThat(lineResponse.getColor()).isEqualTo("blue");
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        lineService.updateLine(이호선.getId(), new LineRequest("9호선", "yellow"));

        // then
        Line line = lineService.findLineById(이호선.getId());

        assertThat(line.getName()).isEqualTo("9호선");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        

        // when
    }

    @DisplayName("지하철 노선에서 구간 제거")
    @Test
    void removeSection() {
        // given
        Line 일호선 = new Line(1L, "1호선", "blue");

        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);

        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));

        // when
        lineService.addSection(일호선.getId(), new SectionRequest(구로역.getId(), 신도림역.getId(), 구로역_신도림역_거리));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 영등포역.getId(), 신도림역_영등포역_거리));
        lineService.deleteSection(일호선.getId(), 영등포역.getId());

        Line line = lineService.findLineById(일호선.getId());

        // then
        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getSections().count()).isEqualTo(1);
    }

    @DisplayName("지하철 노선에서 구간 제거 실패")
    @Test
    void removeSectionFail() {
        // given
        Line 일호선 = new Line(1L, "1호선", "blue");

        when(stationService.findById(구로역.getId())).thenReturn(구로역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);

        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));

        // when
        lineService.addSection(일호선.getId(), new SectionRequest(구로역.getId(), 신도림역.getId(), 구로역_신도림역_거리));

        // then
        assertThatThrownBy(() -> lineService.deleteSection(일호선.getId(), 영등포역.getId())).isInstanceOf(IllegalArgumentException.class);
    }


    public static class SubwayInfo {
        public static Station 구로역 = new Station(1L, "구로역");
        public static Station 신도림역 = new Station(2L, "신도림역");
        public static Station 영등포역 = new Station(3L, "영등포역");

        public static Line 일호선 = new Line("1호선", "blue");
        public static Line 이호선 = new Line(2L,"1호선", "blue");


        public static int 구로역_신도림역_거리 = 10;
        public static int 신도림역_영등포역_거리 = 15;
    }
}
