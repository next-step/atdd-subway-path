package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
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
class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    private static final Station 잠실역 = new Station(1L, "잠실역");
    private static final Station 강남역 = new Station(2L, "강남역");
    private static final Station 선릉역 = new Station(3L, "선릉역");

    @DisplayName("지하철 노선을 저장한다.")
    @Test
    void saveLine() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.save(any())).thenReturn(일호선);
        when(lineRepository.findAll()).thenReturn(List.of(일호선));
        when(stationService.findById(잠실역.getId())).thenReturn(잠실역);
        when(stationService.findById(선릉역.getId())).thenReturn(선릉역);

        LineResponse 일호선_응답 = lineService.saveLine(new LineRequest("일호선", "green", 잠실역.getId(), 선릉역.getId(), 10));

        assertThat(lineService.showLines()).containsExactly(일호선_응답);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        Line 일호선 = new Line(1L, "일호선", "green");
        Line 이호선 = new Line(2L, "이호선", "green");
        when(lineRepository.findAll()).thenReturn(List.of(일호선, 이호선));

        assertThat(lineService.showLines()).containsExactly(LineResponse.from(일호선), LineResponse.from(이호선));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(일호선));

        assertThat(lineService.findById(일호선.getId())).isEqualTo(LineResponse.from(일호선));
    }

    @DisplayName("지하철 노선 정보를 업데이트한다.")
    @Test
    void updateLine() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(일호선));

        lineService.updateLine(일호선.getId(), new LineRequest("3호선", "blue", null, null, 0));

        assertThat(일호선.getName()).isEqualTo("3호선");
        assertThat(일호선.getColor()).isEqualTo("blue");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.findAll()).thenReturn(new ArrayList<>());

        lineService.deleteLine(일호선.getId());

        assertThat(lineService.showLines()).doesNotContain(LineResponse.from(일호선));
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));
        when(stationService.findById(잠실역.getId())).thenReturn(잠실역);
        when(stationService.findById(선릉역.getId())).thenReturn(선릉역);

        lineService.addSection(일호선.getId(), new SectionRequest(잠실역.getId(), 선릉역.getId(), 10));

        LineResponse 일호선_응답 = lineService.findById(일호선.getId());
        assertThat(일호선_응답.getStations()).containsExactly(StationResponse.from(잠실역), StationResponse.from(선릉역));

    }

    @DisplayName("지하철 노선에서 구간을 삭제한다.")
    @Test
    void deleteSection() {
        Line 일호선 = new Line(1L, "일호선", "green");
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));
        when(stationService.findById(잠실역.getId())).thenReturn(잠실역);
        when(stationService.findById(선릉역.getId())).thenReturn(선릉역);
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        lineService.addSection(일호선.getId(), new SectionRequest(잠실역.getId(), 선릉역.getId(), 10));
        lineService.addSection(일호선.getId(), new SectionRequest(선릉역.getId(), 강남역.getId(), 10));

        lineService.deleteSection(일호선.getId(), 강남역.getId());

        assertThat(lineService.findById(일호선.getId())
                .getStations()).doesNotContain(StationResponse.from(강남역));
    }
}
