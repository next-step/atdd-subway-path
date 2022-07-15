package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static nextstep.subway.unit.LineServiceMockTest.Stub.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("지하철 구간 관련 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(신대방역.getId())).thenReturn(신대방역);
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(신대방역.getId(), 신림역.getId(), 8));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        // given
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        LineService lineService = new LineService(lineRepository, stationService);
        이호선.addSection(신대방역, 신림역, 8);

        // when
        lineService.deleteSection(이호선.getId(), 이호선.getLastDownStation().getId());

        // then
        assertThat(이호선.getStations()).contains(구로디지털단지역, 신대방역);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void saveLine() {
        // given
        when(stationService.findById(구로디지털단지역.getId())).thenReturn(구로디지털단지역);
        when(stationService.findById(신대방역.getId())).thenReturn(신대방역);
        when(lineRepository.save(any())).thenReturn(이호선);

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        LineResponse response = lineService.saveLine(new LineRequest("2호선", "green", 구로디지털단지역.getId(), 신대방역.getId(), 10));

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("2호선");
        assertThat(response.getColor()).isEqualTo("green");
        assertThat(response.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        lineService.updateLine(이호선.getId(), new LineRequest("신분당선", "red"));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.empty());
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> lineService.findLineById(이호선.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static class Stub {
        static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        static final Station 신대방역 = new Station("신대방역");
        static final Station 신림역 = new Station("신림역");
        static final Line 이호선 = new Line("2호선", "green", 구로디지털단지역, 신대방역, 10);

        static {
            ReflectionTestUtils.setField(구로디지털단지역, "id", 1L);
            ReflectionTestUtils.setField(신대방역, "id", 2L);
            ReflectionTestUtils.setField(신림역, "id", 3L);
            ReflectionTestUtils.setField(이호선, "id", 1L);
        }

    }
}
