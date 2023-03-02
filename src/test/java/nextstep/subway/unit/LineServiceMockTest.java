package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;


    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 강남_2호선 = new Line("강남_2호선", "green");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        int distance = 10;

        when(lineRepository.findById(강남역.getId())).thenReturn(Optional.of(강남_2호선));
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);

        // when
        // lineService.addSection 호출
        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), distance));

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(강남_2호선.getId());
        Line 검증하려는_노선 = lineRepository.findById(lineResponse.getId()).get();
        List<Section> 검증하려는_노선의_구간_목록 = 검증하려는_노선.getSections();

        assertAll(
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getUpStation().getId()).isEqualTo(강남역.getId()),
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getDownStation().getId()).isEqualTo(역삼역.getId()),
                () -> assertThat(검증하려는_노선의_구간_목록.get(0).getDistance()).isEqualTo(distance)
        );

    }
}
