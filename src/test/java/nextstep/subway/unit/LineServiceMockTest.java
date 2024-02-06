package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private LineService lineService;

    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 선릉역 = new Station(3L, "선릉역");
    private final Line 강남선 = new Line(1L, "강남선", "red", 강남역, 역삼역, 10L);
    private final Section 역삼_선릉 = new Section(강남선, 역삼역, 선릉역, 10L);


    @DisplayName("구간 1개 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(강남선.getId())).thenReturn(Optional.of(강남선));
        when(stationRepository.findById(역삼역.getId())).thenReturn(Optional.of(역삼역));
        when(stationRepository.findById(선릉역.getId())).thenReturn(Optional.of(선릉역));
        // when
        // lineService.addSection 호출
        lineService.addSection(강남선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 10L));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.showLine(강남선.getId()).getStations()).hasSize(3);
    }

    @DisplayName("마지막 구간 1개 제거")
    @Test
    void deleteSection() {
        // given
        강남선.addSection(역삼_선릉);

        when(lineRepository.findById(강남선.getId())).thenReturn(Optional.of(강남선));

        // when
        lineService.deleteSection(강남선.getId(), 선릉역.getId());

        // then
        assertThat(lineService.showLine(강남선.getId()).getStations()).hasSize(2);
    }
}

