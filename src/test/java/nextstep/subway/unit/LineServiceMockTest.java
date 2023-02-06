package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long 이호선 = 1L;
        Long 삼성역 = 1L;
        Long 역삼역 = 2L;

        when(lineRepository.findById(이호선)).thenReturn(Optional.of(new Line("2호선", "green")));
        when(stationService.findById(삼성역)).thenReturn(new Station("삼성역"));
        when(stationService.findById(역삼역)).thenReturn(new Station("역삼역"));
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        addSection(lineService, 이호선, 삼성역, 역삼역);

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(이호선).orElseThrow();
        assertAll(() -> assertThat(line.getSections().size()).isEqualTo(1)
                , () -> assertThat(line.getAllStations().stream().map(Station::getName))
                        .containsOnly("삼성역", "역삼역")
        );
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long 이호선 = 1L;
        Long 삼성역 = 1L;
        Long 선릉역 = 2L;
        Long 역삼역 = 3L;

        when(lineRepository.findById(이호선)).thenReturn(Optional.of(new Line("2호선", "green")));
        when(stationService.findById(삼성역)).thenReturn(new Station("삼성역"));
        when(stationService.findById(선릉역)).thenReturn(new Station("선릉역"));
        when(stationService.findById(역삼역)).thenReturn(new Station("역삼역"));
        LineService lineService = new LineService(lineRepository, stationService);

        addSection(lineService, 이호선, 삼성역, 선릉역);
        addSection(lineService, 이호선, 선릉역, 역삼역);

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(이호선, 역삼역);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(이호선).orElseThrow();
        assertAll(() -> assertThat(line.getSections().size()).isEqualTo(1)
                , () -> assertThat(line.getAllStations().stream().map(Station::getName))
                        .containsOnly("삼성역", "선릉역")
        );
    }

    private static void addSection(LineService lineService, Long lineId, Long upStationId, Long downStationId) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);
        lineService.addSection(lineId, sectionRequest);
    }
}
