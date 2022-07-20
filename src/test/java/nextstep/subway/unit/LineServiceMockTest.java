package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    @DisplayName("노선을 등록합니다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("이호선", "bg-green-600")));
        given(stationService.findById(1L)).willReturn(new Station("강남역"));
        given(stationService.findById(2L)).willReturn(new Station("역삼역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 6));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getStations()).hasSize(2);
    }

    @Test
    @DisplayName("노선 목록 조회 합니다")
    void showSection() {
        given(lineRepository.findAll()).willReturn(List.of(
            new Line("1호선", "bg-blue-600"),
            new Line("2호선", "bg-gree")
        ));

        List<LineResponse> 노선목록 = lineService.showLines();

        assertThat(노선목록).hasSize(2);
    }

    @Test
    @DisplayName("노선 조회합니다.")
    void findLine() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("1호선", "bg-blue-600")));

        LineResponse 일호선 = lineService.findById(1L);

        assertAll(() -> {
            assertThat(일호선.getName()).isEqualTo("1호선");
            assertThat(일호선.getColor()).isEqualTo("bg-blue-600");
        });
    }

    @Test
    @DisplayName("노선을 수정 합니다.")
    void updateSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("1호선", "bg-blue-600")));

        lineService.updateLine(1L, new LineRequest("2호선", "bg-green-600", 1L, 2L, 6));

        LineResponse 일호선 = lineService.findById(1L);
        assertAll(() -> {
            assertThat(일호선.getName()).isEqualTo("2호선");
            assertThat(일호선.getColor()).isEqualTo("bg-green-600");
        });
    }

    @Test
    @DisplayName("노선을 삭제 합니다.")
    void removeLine() {
        lineService.deleteLine(1L);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(1L);
        });
    }

    @Test
    @DisplayName("구간을 삭제 합니다.")
    void removeSection() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(2L)).willReturn(역삼역);

        lineService.deleteSection(1L, 2L);

        assertThat(lineService.findById(1L).getStations()).isEmpty();
    }

    @Test
    @DisplayName("하행선이 아닌 곳을 삭제시 에러가 발생한다.")
    void removeException() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);

        assertThatIllegalArgumentException().isThrownBy(() ->
            lineService.deleteSection(1L, 1L)
        );
    }

}
