package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private static String NEW_BUN_DANG = "신분당선";
    private static String BG_RED_600 = "bg-red-600";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @Test
    @DisplayName("기존 노선에 한 구간을 추가")
    void addOneSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(신논현역));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(Line.of(NEW_BUN_DANG, BG_RED_600)));
        LineService lineService = new LineService(lineRepository, stationRepository);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, SectionRequest.of(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line findLine = lineService.findLineById(1L);

        assertAll(
                () -> assertThat(findLine.getName()).isEqualTo(NEW_BUN_DANG),
                () -> assertThat(findLine.getColor()).isEqualTo(BG_RED_600),
                () -> assertThat(findLine.getSections()).hasSize(1),
                () -> assertThat(findLine.allStations()).extracting("name").containsExactly("강남역", "신논현역")
        );
    }

    @Test
    @DisplayName("기존 노선에 다양한 방법으로 구간을 추가")
    void addSectionDifferentWays() {
        // given
        // 지하철 역 설정, 노선 설정
        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(신논현역));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(정자역));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(판교역));
        when(stationRepository.findById(5L)).thenReturn(Optional.of(이매역));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(Line.of(NEW_BUN_DANG, BG_RED_600)));
        LineService lineService = new LineService(lineRepository, stationRepository);

        // when
        // lineService.addSection 호출 [다양한 방법으로 노선 추가(중간, 맨앞, 맨뒤)]
        lineService.addSection(1L, SectionRequest.of(1L, 2L, 10));
        lineService.addSection(1L, SectionRequest.of(1L, 3L, 4));
        lineService.addSection(1L, SectionRequest.of(4L, 1L, 5));
        lineService.addSection(1L, SectionRequest.of(2L, 5L, 7));

        // then
        // line.findLineById 메서드를 통해 검증 (노선의 순서는 상행부터 하행까지 정렬되어 있음)
        Line findLine = lineService.findLineById(1L);

        assertAll(
                () -> assertThat(findLine.getSections()).hasSize(4),
                () -> assertThat(findLine.allStations()).extracting("name")
                        .containsExactly("판교역", "강남역", "정자역", "신논현역", "이매역")
        );
    }
}
