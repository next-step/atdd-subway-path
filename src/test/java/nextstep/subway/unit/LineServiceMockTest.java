package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
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

    Station 강남역;
    Station 역삼역;
    Station 선릉역;

    Line 일호선;
    Line 이호선;

    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");

        일호선 = new Line("1호선", "bg-blue-600");
        이호선 = new Line("2호선", "bg-green-600");
    }

    @Test
    @DisplayName("지하철 구간을 등록합니다.")
    void addSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);
        given(stationService.findById(2L)).willReturn(역삼역);

        lineService.addSection(1L, new SectionRequest(1L, 2L, 6));

        LineResponse 이호선_반환값 = lineService.findById(1L);
        LineResponse 비교값 = createLineResponse("2호선", "bg-green-600", List.of(강남역, 역삼역));

        assertThat(이호선_반환값).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철 노선 목록 조회 합니다")
    void showSection() {
        given(lineRepository.findAll()).willReturn(List.of(
            일호선,
            이호선
        ));

        List<LineResponse> 노선목록 = lineService.showLines();
        List<LineResponse> 비교값 = List.of(createLineResponse("1호선", "bg-blue-600", List.of()),
                createLineResponse("2호선", "bg-green-600", List.of()) );

        assertThat(노선목록).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철 노선을 조회합니다.")
    void findLine() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));

        LineResponse 일호선_반환값 = lineService.findById(1L);

        assertThat(일호선_반환값).isEqualTo(createLineResponse("1호선", "bg-blue-600", List.of()));
    }

    @Test
    @DisplayName("지하철 노선을 수정 합니다.")
    void updateSection() {
        given(lineRepository.findById(1L)).willReturn(Optional.of(일호선));

        lineService.updateLine(1L, new LineRequest("2호선", "bg-green-600", 1L, 2L, 6));

        LineResponse 일호선_반환값 = lineService.findById(1L);

        assertThat(일호선_반환값).isEqualTo(createLineResponse("2호선", "bg-green-600", List.of()));
    }

    @Test
    @DisplayName("지하철 노선을 삭제 합니다.")
    void removeLine() {
        given(lineRepository.save(any(Line.class))).willReturn(new Line("1호선", "bg-blue-500"));
        given(lineRepository.findById(1L)).willReturn(Optional.empty());

        lineService.saveLine(new LineRequest("1호선", "bg-blue-500", 강남역.getId(), 역삼역.getId(), 6));
        lineService.deleteLine(1L);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            lineService.findById(1L);
        });
    }

    @Test
    @DisplayName("지하철 구간을 삭제 합니다.")
    void removeSection() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(2L)).willReturn(역삼역);

        lineService.deleteSection(1L, 2L);

        assertThat(lineService.findById(1L).getStations()).isEmpty();
    }

    @Test
    @DisplayName("하행선이 아닌 곳을 삭제시 에러가 발생한다.")
    void removeException() {
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 6));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 4));

        given(lineRepository.findById(1L)).willReturn(Optional.of(이호선));
        given(stationService.findById(1L)).willReturn(강남역);

        assertThatIllegalArgumentException().isThrownBy(() ->
            lineService.deleteSection(1L, 1L)
        );
    }

    private LineResponse createLineResponse(String name, String color, List<Station> stationName) {
        return LineResponse.builder()
            .name(name)
            .color(color)
            .stations(stationName.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()))
            .build();
    }

}
